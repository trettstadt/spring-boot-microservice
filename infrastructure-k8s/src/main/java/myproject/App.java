package myproject;

import com.pulumi.Context;
import com.pulumi.Pulumi;
import com.pulumi.core.Output;
import com.pulumi.kubernetes.Provider;
import com.pulumi.kubernetes.ProviderArgs;
import com.pulumi.kubernetes.helm.v3.Release;
import com.pulumi.kubernetes.helm.v3.ReleaseArgs;
import com.pulumi.kubernetes.helm.v3.inputs.RepositoryOptsArgs;
import com.pulumi.resources.CustomResourceOptions;
import com.pulumi.resources.StackReference;
import com.pulumi.resources.StackReferenceArgs;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class App {
  public static void main(String[] args) {
    Pulumi.run(App::stack);
  }

  public static void stack(Context ctx) {
    var config = ctx.config();
    var infraStackName = config.require("infraStackName");
    var kubeconfigPath = config.get("kubeconfigPath").orElse("~/.kube/config-k3s-demo");
    var certManagerVersion = config.get("certManagerVersion").orElse("v1.16.2");
    var traefikVersion = config.get("traefikVersion").orElse("v34.4.1");

    // Reference the infrastructure stack to establish dependency ordering
    var infraStack = new StackReference("infra-stack",
        StackReferenceArgs.builder()
            .name(infraStackName)
            .build());

    var masterIp = infraStack.requireOutput("masterPublicIp");

    // Read the kubeconfig written by the Ansible K3s bootstrap playbook
    var resolvedPath = kubeconfigPath.replace("~", System.getProperty("user.home"));
    String kubeconfig;
    try {
      kubeconfig = Files.readString(Paths.get(resolvedPath));
    } catch (Exception e) {
      throw new RuntimeException("Failed to read kubeconfig from " + resolvedPath
          + ". Run the Ansible playbook first.", e);
    }

    // Configure the Kubernetes provider to target the K3s cluster
    var k8sProvider = new Provider("k3s", ProviderArgs.builder()
        .kubeconfig(kubeconfig)
        .build());

    var opts = CustomResourceOptions.builder()
        .provider(k8sProvider)
        .build();

    // Install cert-manager via Helm (CRDs included)
    var certManager = new Release("cert-manager", ReleaseArgs.builder()
        .name("cert-manager")
        .namespace("cert-manager")
        .createNamespace(true)
        .repositoryOpts(RepositoryOptsArgs.builder()
            .repo("https://charts.jetstack.io")
            .build())
        .chart("cert-manager")
        .version(certManagerVersion)
        .values(Map.of(
            "installCRDs", true,
            "replicaCount", 1
        ))
        .build(), opts);

    // Install Traefik via Helm (default ingress controller)
    var traefik = new Release("traefik", ReleaseArgs.builder()
        .name("traefik")
        .namespace("traefik")
        .createNamespace(true)
        .repositoryOpts(RepositoryOptsArgs.builder()
            .repo("https://helm.traefik.io/traefik")
            .build())
        .chart("traefik")
        .version(traefikVersion)
        .values(Map.of(
            "service", Map.of("type", "LoadBalancer"),
            "ingressClass", Map.of(
                "enabled", true,
                "isDefaultClass", true
            ),
            "ports", Map.of(
                "web", Map.of(
                    "redirections", Map.of(
                        "entryPoint", Map.of(
                            "to", "websecure",
                            "scheme", "https",
                            "permanent", true
                        )
                    )
                )
            ),
            "providers", Map.of(
                "kubernetesIngress", Map.of(
                    "publishedService", Map.of("enabled", true)
                )
            )
        ))
        .build(), opts);

    ctx.export("masterPublicIp", masterIp);
    ctx.export("certManagerNamespace", certManager.namespace());
    ctx.export("traefikNamespace", traefik.namespace());
  }
}
