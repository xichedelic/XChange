package org.knowm.xchange.uabpervesk;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.QueryParam;
import java.io.IOException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.knowm.xchange.uabpervesk.dto.BankeraException;
import org.knowm.xchange.uabpervesk.dto.BankeraToken;
import org.knowm.xchange.uabpervesk.dto.marketdata.BankeraMarketInfo;
import org.knowm.xchange.uabpervesk.dto.marketdata.BankeraOrderBook;
import org.knowm.xchange.uabpervesk.dto.marketdata.BankeraTickerResponse;
import org.knowm.xchange.uabpervesk.dto.marketdata.BankeraTradesResponse;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public interface Bankera {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("/oauth/token")
  BankeraToken getToken(
      @QueryParam("client_id") String clientId,
      @QueryParam("client_secret") String clientSecret,
      @QueryParam("grant_type") String grantType,
      @QueryParam("scope") String scope
  )
      throws BankeraException;

  @GET
  @Path("/tickers/{market}")
  BankeraTickerResponse getMarketTicker(@PathParam("market") String market)
      throws BankeraException, IOException;

  @GET
  @Path("/orderbooks/{market}")
  BankeraOrderBook getOrderbook(@PathParam("market") String market)
      throws BankeraException, IOException;

  @GET
  @Path("/trades/{market}")
  BankeraTradesResponse getRecentTrades(@PathParam("market") String market)
      throws BankeraException, IOException;

  @GET
  @Path("/general/info")
  BankeraMarketInfo getMarketInfo() throws BankeraException, IOException;
}