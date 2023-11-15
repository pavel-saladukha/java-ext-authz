package de.dkwr.authzexample;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.envoyproxy.envoy.config.core.v3.HeaderValue;
import io.envoyproxy.envoy.config.core.v3.HeaderValueOption;
import io.envoyproxy.envoy.service.auth.v3.AuthorizationGrpc;
import io.envoyproxy.envoy.service.auth.v3.CheckResponse;
import io.envoyproxy.envoy.service.auth.v3.OkHttpResponse;

import java.util.Map;
import java.util.Objects;

/**
 * Implementation of the ext-authz service for Envoy.
 * @see <a href="https://github.com/envoyproxy/envoy/blob/main/api/envoy/service/auth/v3/external_auth.proto">...</a>
 */
public class AuthorizationServiceImpl extends AuthorizationGrpc.AuthorizationImplBase {
    @Override
    public void check(io.envoyproxy.envoy.service.auth.v3.CheckRequest request,
                      io.grpc.stub.StreamObserver<io.envoyproxy.envoy.service.auth.v3.CheckResponse> done) {
        Map<String, String> headers = request.getAttributes().getRequest().getHttp().getHeadersMap();

        System.out.println("header value = " + headers.get("x-ext-authz"));
        System.out.println("if value " + Objects.equals(headers.get("x-ext-authz"), "allow"));

        if (Objects.equals(headers.get("x-ext-authz"), "allow") == Boolean.FALSE) {
            done.onNext(
                    CheckResponse
                            .newBuilder()
                            .setStatus(Status.newBuilder().setCode(Code.UNAUTHENTICATED_VALUE).build())
                            .setDeniedResponse(CheckResponse.getDefaultInstance().getDeniedResponse())
                    .build()
            );

            done.onCompleted();
            return;
        }

        done.onNext(CheckResponse
                .newBuilder()
                .setStatus(Status.newBuilder().setCode(Code.OK_VALUE).build())
                .setOkResponse(OkHttpResponse.newBuilder()
                        .addHeaders(
                                HeaderValueOption.newBuilder().setHeader(
                                        HeaderValue.newBuilder().setKey("oh-my-gud").setValue("valsiruem").build()
                                ).build()
                        ).build())
                .build());

        done.onCompleted();

    }
}
