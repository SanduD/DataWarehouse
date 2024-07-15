package ro.uvt.info.dw.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Service
public class NasdaqDataService {

    private final String apiKey = "uUEvH5bz3Y3NgATogjr8";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getFinancialData(String dataset, String ticker) throws IOException {
        String url = "https://data.nasdaq.com/api/v3/datasets/" + dataset + "/" + ticker + ".json?api_key=" + apiKey;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                String json = EntityUtils.toString(response.getEntity());
                return objectMapper.readTree(json);
            }
        }
    }
}
