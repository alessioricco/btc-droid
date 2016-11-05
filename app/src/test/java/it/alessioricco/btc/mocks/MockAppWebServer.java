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
    final Dispatcher dispatcher = new Dispatcher() {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

            if (request.getPath().equals("/v1/markets.json")){

                final String response = MockBitcoinCharts.getRawResponse();
                return new MockResponse()
                        .setResponseCode(200)
                        .addHeader("Content-Type", "application/json; charset=utf-8")
                        .addHeader("Cache-Control", "no-cache")
                        .setBody(response);

            }
//             else if (request.getPath().equals("v1/check/version/")){
//                return new MockResponse().setResponseCode(200).setBody("version=9");
//
//            } else if (request.getPath().equals("/v1/profile/info")) {
//                return new MockResponse().setResponseCode(200).setBody("{\\\"info\\\":{\\\"name\":\"Lucas Albuquerque\",\"age\":\"21\",\"gender\":\"male\"}}");
//            }
            return new MockResponse().setResponseCode(404);

        }
    };

    public MockAppWebServer() {
        mockWebServer = new MockWebServer();
        mockWebServer.setDispatcher(dispatcher);
    }

    public void start() throws IOException {
        mockWebServer.start();
    }

    public void shutdown(){
        try {
            mockWebServer.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
