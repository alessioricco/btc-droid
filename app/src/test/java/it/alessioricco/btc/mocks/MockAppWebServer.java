package it.alessioricco.btc.mocks;

import java.io.IOException;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * this is a mock web server that will handle all the
 * mock calls from the app
 */
public class MockAppWebServer {

    final MockWebServer mockWebServer;

    MockResponse newResponse;
    public void setMockResponse(final MockResponse mockResponse) {
        newResponse = mockResponse;
    }

    final Dispatcher dispatcher = new Dispatcher() {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

            if (newResponse != null) {
                return newResponse;  //new MockResponse().setResponseCode(responseCode);
            }

            if (request.getPath().equals("/v1/markets.json")){
                final String response = MockBitcoinCharts.getMarketsJsonRawResponse();
                return new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache")
                        .setBody(response);
            }
             else if (request.getPath().startsWith("/v1/trades.csv")){
                final String response = MockBitcoinCharts.getHistoryCSVRawResponse();
                return new MockResponse()
                        .setResponseCode(200)
                        .setBody(response);

            }
            //HTTP/1.1 404 Client Error
            return new MockResponse().setResponseCode(404);

        }
    };

    public MockAppWebServer() {
        mockWebServer = new MockWebServer();
        //super();
        mockWebServer.setDispatcher(dispatcher);
    }

    public void start() throws IOException {
        mockWebServer.start();
    }

    public MockWebServer getMockWebServer() {
        return mockWebServer;
    }

    public void shutdown(){
        try {
            mockWebServer.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
