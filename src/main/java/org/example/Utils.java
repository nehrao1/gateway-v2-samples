package org.example;

import com.azure.cosmos.CosmosDiagnostics;
import com.azure.cosmos.CosmosDiagnosticsContext;
import com.azure.cosmos.CosmosDiagnosticsRequestInfo;

import java.util.Collection;

public class Utils {
    private static final String gatewayV2Indicator = ":10250/";

    static void validateThinClientEndpointUsed(CosmosDiagnostics diagnostics) {
        try {
            if (diagnostics == null) {
                throw new IllegalArgumentException("CosmosDiagnostics cannot be null");
            }

            CosmosDiagnosticsContext ctx = diagnostics.getDiagnosticsContext();
            if (ctx == null) {
                throw new IllegalStateException("CosmosDiagnosticsContext cannot be null");
            }

            Collection<CosmosDiagnosticsRequestInfo> requests = ctx.getRequestInfo();
            if (requests == null || requests.isEmpty()) {
                throw new IllegalStateException("No request information found in CosmosDiagnosticsContext");
            }

            for (CosmosDiagnosticsRequestInfo requestInfo : requests) {
                if (requestInfo.getEndpoint().contains(gatewayV2Indicator)) {
                    System.out.println("Gateway 2.0 endpoint was used for request: " + requestInfo.getEndpoint());
                    return;
                }
            }

            throw new RuntimeException("No request targeting Gateway 2.0 endpoint.");
        } catch (Error e) {
            System.err.println("Error validating Gateway 2.0 endpoint: " + e.getMessage());
            throw e;
        }
    }
}
