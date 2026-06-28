package myproject;

import com.pulumi.Context;
import com.pulumi.Pulumi;
import com.pulumi.core.Output;
import com.pulumi.hcloud.Firewall;
import com.pulumi.hcloud.FirewallArgs;
import com.pulumi.hcloud.Network;
import com.pulumi.hcloud.NetworkArgs;
import com.pulumi.hcloud.NetworkSubnet;
import com.pulumi.hcloud.NetworkSubnetArgs;
import com.pulumi.hcloud.Server;
import com.pulumi.hcloud.ServerArgs;
import com.pulumi.hcloud.SshKey;
import com.pulumi.hcloud.SshKeyArgs;
import com.pulumi.hcloud.Zone;
import com.pulumi.hcloud.ZoneArgs;
import com.pulumi.hcloud.ZoneRecord;
import com.pulumi.hcloud.ZoneRecordArgs;
import com.pulumi.hcloud.inputs.FirewallRuleArgs;
import com.pulumi.hcloud.inputs.ServerNetworkArgs;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class App {
  public static void main(String[] args) {
    Pulumi.run(App::stack);
  }

  public static void stack(Context ctx) {
    String myHomeIp = "95.90.187.201/32";

    String publicKey;
    try {
      String sshKeyPath = System.getProperty("user.home") + "/.ssh/id_rsa.pub";
      publicKey = Files.readString(Paths.get(sshKeyPath)).trim();
    } catch (Exception e) {
      throw new RuntimeException("Failed to read local SSH public key.", e);
    }

    var k3sSshKey = new SshKey("k3s-ssh-key", SshKeyArgs.builder()
        .name("k3s-cluster-key")
        .publicKey(publicKey)
        .build());

    // 1. Create the Private Network (VPC)
    var privateNetwork = new Network("k3s-vnet", NetworkArgs.builder()
        .name("k3s-private-network")
        .ipRange("10.0.0.0/16")
        .build());

    // 2. Create a Subnet inside the Private Network for our Cluster
    var clusterSubnet = new NetworkSubnet("k3s-subnet", NetworkSubnetArgs.builder()
        .networkId(privateNetwork.id().applyValue(Integer::parseInt))
        .type("cloud")
        .networkZone("eu-central")
        .ipRange("10.0.1.0/24")
        .build());

    // 3. Define the Secure Firewall
    // Notice that internal cluster ports (8472, 10250) are strictly limited to the 10.0.0.0/16 range
    var clusterFirewall = new Firewall("k3s-firewall", FirewallArgs.builder()
        .name("k3s-cluster-firewall")
        .rules(List.of(
            FirewallRuleArgs.builder()
                .direction("in")
                .protocol("tcp")
                .port("22")
                .sourceIps(List.of(myHomeIp))
                .description("Allow SSH from home office")
                .build(),

            FirewallRuleArgs.builder()
                .direction("in")
                .protocol("tcp")
                .port("6443")
                .sourceIps(List.of(myHomeIp)) // For external kubectl management
                .description("Allow Kubectl from home office")
                .build(),

            FirewallRuleArgs.builder()
                .direction("in")
                .protocol("tcp")
                .port("80")
                .sourceIps(List.of("0.0.0.0/0", "::/0"))
                .description("Public HTTP traffic")
                .build(),

            FirewallRuleArgs.builder()
                .direction("in")
                .protocol("tcp")
                .port("443")
                .sourceIps(List.of("0.0.0.0/0", "::/0"))
                .description("Public HTTPS traffic")
                .build(),

            // SECURE INTER-NODE TRAFFIC: Only allow IPs originating within our private network
            FirewallRuleArgs.builder()
                .direction("in")
                .protocol("udp")
                .port("8472")
                .sourceIps(List.of("10.0.0.0/16"))
                .description("Restrict Flannel VXLAN to Private Network")
                .build(),

            FirewallRuleArgs.builder()
                .direction("in")
                .protocol("tcp")
                .port("10250")
                .sourceIps(List.of("10.0.0.0/16"))
                .description("Restrict Kubelet Traffic to Private Network")
                .build(),

            FirewallRuleArgs.builder()
                .direction("in")
                .protocol("tcp")
                .port("6443")
                .sourceIps(List.of("10.0.0.0/16")) // Allows internal worker-to-master API communication
                .description("Allow internal node registration")
                .build()
        ))
        .build());

    Output<List<Integer>> firewallIdsOutput = clusterFirewall.id().applyValue(idStr ->
        List.of(Integer.parseInt(idStr))
    );

    // 4. Provision Servers Attached to both the Firewall and the Private Network
    var k3sMaster = new Server("k3s-master", ServerArgs.builder()
        .name("k3s-master")
        .serverType("cx23")
        .image("ubuntu-24.04")
        .location("nbg1")
        .sshKeys("k3s-cluster-key")
        .firewallIds(firewallIdsOutput)
        .networks(ServerNetworkArgs.builder()
            .networkId(privateNetwork.id().applyValue(Integer::parseInt))
            .ip("10.0.1.10") // Assign a static private IP to the Master
            .build())
        .build());

    var k3sWorker = new Server("k3s-worker", ServerArgs.builder()
        .name("k3s-worker")
        .serverType("cx23")
        .image("ubuntu-24.04")
        .location("nbg1")
        .sshKeys("k3s-cluster-key")
        .firewallIds(firewallIdsOutput)
        .networks(ServerNetworkArgs.builder()
            .networkId(privateNetwork.id().applyValue(Integer::parseInt))
            .ip("10.0.1.11") // Assign a static private IP to the Worker
            .build())
        .build());

    // 5. Create DNS zone for retdemo.de
    var dnsZone = new Zone("retdemo-zone", ZoneArgs.builder()
        .name("retdemo.de")
        .mode("primary")
        .ttl(300)
        .build());

    // 6. Wildcard A record *.retdemo.de pointing to the ingress (master public IP)
    var wildcardRecord = new ZoneRecord("wildcard-retdemo", ZoneRecordArgs.builder()
        .zone(dnsZone.id())
        .name("*")
        .type("A")
        .value(k3sMaster.ipv4Address())
        .build());

    ctx.export("masterPublicIp", k3sMaster.ipv4Address());
    ctx.export("workerPublicIp", k3sWorker.ipv4Address());
    ctx.export("dnsZoneId", dnsZone.id());
    ctx.export("dnsZoneName", dnsZone.name());
  }
}