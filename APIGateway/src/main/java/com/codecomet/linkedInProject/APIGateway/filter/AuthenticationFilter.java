package com.codecomet.linkedInProject.APIGateway.filter;


import com.codecomet.linkedInProject.APIGateway.JWTService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JWTService jwtService;

    public AuthenticationFilter(JWTService jwtService){
        super(Config.class);
        this.jwtService=jwtService;
    }

    // here we will extract the userId from the jwt token and pass it to the other services
    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {

            log.info("Auth request: {}", exchange.getRequest().getURI());

            final String tokenHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            //if we get the token as null or not start with "Bearer" (faulty) than make the req unauthorized
            if(tokenHeader == null || !tokenHeader.startsWith("Bearer")){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            final String token = tokenHeader.split("Bearer ")[1];

            try{
                String userId = jwtService.getUserIdFromToken(token);
                // here we have mutated the exchange and added the userid header
                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(r-> r.header("X-User-Id", userId))
                        .build();

                // returning our mutated exchange
                return chain.filter(mutatedExchange);
            }
            catch (JwtException e){
                log.error("JWT Exception : {}",e.getLocalizedMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }));
    }

    public static class Config{

    }
}
