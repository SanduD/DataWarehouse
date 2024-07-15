package ro.uvt.info.dw.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Table("data")
public class Data {

    @PrimaryKeyClass
    public static class DataKey {
        @PrimaryKeyColumn(name = "assetId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
        private int assetId;

        @PrimaryKeyColumn(name = "sourceId", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
        private int sourceId;

        @PrimaryKeyColumn(name = "businessDate", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
        private String businessDate;

        @PrimaryKeyColumn(name = "systemDate", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
        private Date systemDate;

        // Getters and setters
        public int getAssetId() {
            return assetId;
        }

        public void setAssetId(int assetId) {
            this.assetId = assetId;
        }

        public int getSourceId() {
            return sourceId;
        }

        public void setSourceId(int sourceId) {
            this.sourceId = sourceId;
        }

        public String getBusinessDate() {
            return businessDate;
        }

        public void setBusinessDate(String businessDate) {
            this.businessDate = businessDate;
        }

        public Date getSystemDate() {
            return systemDate;
        }

        public void setSystemDate(Date systemDate) {
            this.systemDate = systemDate;
        }
    }

    @Id
    private DataKey key;

    private Map<String, Double> valuesDouble = new HashMap<>();
    private Map<String, Integer> valuesInt = new HashMap<>();
    private Map<String, String> valuesText = new HashMap<>();

    // Getters and setters
    public DataKey getKey() {
        return key;
    }

    public void setKey(DataKey key) {
        this.key = key;
    }

    public String getBusinessDate() {
        return key.getBusinessDate();
    }

    public Map<String, Double> getValuesDouble() {
        return valuesDouble;
    }

    public void setValuesDouble(Map<String, Double> valuesDouble) {
        this.valuesDouble = valuesDouble;
    }

    public Map<String, Integer> getValuesInt() {
        return valuesInt;
    }

    public void setValuesInt(Map<String, Integer> valuesInt) {
        this.valuesInt = valuesInt;
    }

    public Map<String, String> getValuesText() {
        return valuesText;
    }

    public void setValuesText(Map<String, String> valuesText) {
        this.valuesText = valuesText;
    }
}
