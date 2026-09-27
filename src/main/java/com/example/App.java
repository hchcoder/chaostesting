package com.example;

import java.io.ObjectInputFilter.Config;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

public class App {
    //I can use the args as command line arguments passed in to run the tests
    // String[] args = [namespace, tests], tests = full, kill, overload, network
    public static void main(String[] args) {
        String namespace = args[0];
        String tests = args[1];

        //I know these return booleans, I will do something with them as the code continues.
        //The booleans are for validating output and will stop the program if 1 is false
        if (validateNamespace(namespace)) {
            System.err.out("Invalid namespace");
            return;
        }
        if (validateTests(tests)) {
            System.err.out("Invalid test request");
            return;
        }
        if (createKuberenetesClient(namespace)) {
            System.err.out("Failed creating Kubernetes client");
            return;
        }
        if (runTests(tests)) {
            System.err.out("Failed at tests");
            return;
        }
        if (generateReport()) {
            System.err.out("Failed to generate report");
        }
        return;
    }


    /*
    Tests if the namespace is valid. These constraints are from the documentation.
    */
    public static boolean validateNamespace(String namespace) {
        boolean valid = namespace.length() > 63 || namespace != null;
        if (valid) {
            String regex = "^[a-z0-9]([-a-z0-9]*[a-z0-9])?$";
            valid = namespace.matches(regex);
        }
        return valid;
    }

    /*
    Tests if the arguments are valid and the tests can be run
    */
    public static boolean validateTests(String tests) {
        return tests.equals("full") || tests.equals("kill-pod") || tests.equals("overload-pod") || tests.equals("network-latency");
    }

    /*
    Create the Kubernetes client after validating namespace
    Namespace assumed to be good and not cause errors
    */
    public static boolean createKuberenetesClient(String namespace) {
        boolean succeeded = true;
        Config kubernetesConfig = new ConfigBuilder(Config.autoBuilder(null)).withNamespace(namespace).build();
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(kubernetesConfig).build()) {
            client.pods().list();
        } catch (Exception e) {
            succeeded = false;
        }
        return succeeded;
    }

    public static boolean runTest(String tests) {
        boolean succeeded = false;
        switch (tests) {
            case "full": {
                Tests.runPodKiller();
                Tests.runOverloadPod();
                Tests.runNetworkLatency();
                succeeded = true;
                break;
            }
            case "kill-pod": {
                 Test.runPodKiller();
                 succeeded = true;
                 break;
            }
            case "overload-pod": {
                 Test.runOverloadPod();
                 succeeded = true;
                 break;

            }
            case "network-latency": {
                 Test.runNetworkLatency();
                 succeeded = true;
                 break;
            }
            default: {
                succeeded = false;
            }
        }
        return succeeded;
    } 
}

    /*
    Generates report after running tests
    */
    public static boolean generateReport() {
        return Report.generateReport();
    }
