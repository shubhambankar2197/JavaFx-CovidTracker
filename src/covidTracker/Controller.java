package covidTracker;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Controller {
    @FXML
    Label infected,recovered,death,infectedLastUpdated, recoveredLastUpdated, deathLastUpdated;
    @FXML
    LineChart<String, Number> covidData;


    private static final int WINDOW_SIZE =200;
    XYChart.Series<String,Number> series,deathSeries,recoveredSeries;
    String dailyConfirmed, totalConfirmed, totalDeaths, totalRecovered, lastUpdate;

    //OkHttp class to retrieve data from API
    OkHttpClient client = new OkHttpClient().newBuilder().build();
    String url = "https://api.covid19india.org/data.json";
    public String run() throws IOException {
        Request request = new Request.Builder() //sending GET request to the API
                .url(url)
                .method("GET", null)
                .build();
        try( Response response = client.newCall(request).execute()) {
            return response.body().string(); //Response from the API converting to String
        }
    }

    public void init() throws JSONException, IOException, ParseException {
        String jsonData = run();
        JSONObject obj = new JSONObject(jsonData);
        JSONArray arr = obj.getJSONArray("cases_time_series");
        int n =arr.length();

//        label.getStyleClass().setAll("lbl-default");

        //Parsing and Fetching data returned  from API. API returns data in JSON
        dailyConfirmed = arr.getJSONObject(n-1).getString("dailyconfirmed");
        totalConfirmed = arr.getJSONObject(n-1).getString("totalconfirmed");
        totalDeaths = arr.getJSONObject(n-1).getString("totaldeceased");
        totalRecovered = arr.getJSONObject(n-1).getString("totalrecovered");
        lastUpdate = arr.getJSONObject(n-1).getString("date");
        infected.setText(totalConfirmed);
        recovered.setText(totalRecovered);
        death.setText(totalDeaths);

        infected.setStyle("-fx-border-color : indigo; -fx-border-width: 0 0 3 0 ;");
        death.setStyle("-fx-border-color : red; -fx-border-width: 0 0 3 0 ;");
        recovered.setStyle("-fx-border-color : green; -fx-border-width: 0 0 3 0 ;");

        //Setting Label
        infectedLastUpdated.setText("Last updated on : \n" + lastUpdate + LocalDate.now().getYear());
        deathLastUpdated.setText("Last updated on : \n" + lastUpdate + LocalDate.now().getYear());
        recoveredLastUpdated.setText("Last updated on : \n" + lastUpdate + LocalDate.now().getYear());

//
//        infectedLastUpdated.setText(lastUpdate);
//        deathLastUpdated.setText(lastUpdate);
//        recoveredLastUpdated.setText(lastUpdate);



        //Plotting Chart with data from API

        covidData.getXAxis().setLabel("Date (last 200 Days)");
        covidData.getYAxis().setLabel("Cases (in thousands)");

        // Creating a XYSeries to plot the line chart
        series = new XYChart.Series<String, Number>();
        deathSeries = new XYChart.Series<String, Number>();
        recoveredSeries = new XYChart.Series<String, Number>();

        //Setting name to Series
        series.setName("Infected");
        deathSeries.setName("Deaths");
        recoveredSeries.setName("Recovered");
        String text;
        int infectedSer = 0, recoveredSer=0, k=0 ,recoveredData;
        int deaths=0,deathSer = 0,j=0;
        //SimpleDateFormat format = new SimpleDateFormat("dd MMMM ");

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("dd MMMM ")
                .parseDefaulting(ChronoField.YEAR, 2020)
                .toFormatter(Locale.US);

        LocalDate date;
        LocalDate date2,date3;
        boolean x = true, y = true;
        for(int i=0; i<n; i++) {
            infectedSer = arr.getJSONObject(i).getInt("totalconfirmed");
            deaths = arr.getJSONObject(i).getInt("totaldeceased");
            recoveredData = arr.getJSONObject(i).getInt("totalrecovered");
            text = arr.getJSONObject(i).getString("date");
            date = LocalDate.parse(text,formatter);
            if(deaths>0 && x) {
                j = i;
                date2 = date;
                x = false;
            }

            if(recoveredData > 0 && y) {
                k = i;
                date3 = date;
                y = false;
            }

            series.getData().add(new XYChart.Data<String, Number>(date.toString(), infectedSer));
            if(series.getData().size() > WINDOW_SIZE){
                series.getData().remove(0);
            }


        }

        for(int i = j; i<n; i++) {
            deathSer = arr.getJSONObject(i).getInt("totaldeceased");
            text = arr.getJSONObject(i).getString("date");
            date2 = LocalDate.parse(text,formatter);

            deathSeries.getData().add(new XYChart.Data<String, Number>(date2.toString(), deathSer));
            if(deathSeries.getData().size() > WINDOW_SIZE){
                deathSeries.getData().remove(0);
            }
        }

        for(int i = k; i<n; i++) {
            recoveredSer = arr.getJSONObject(i).getInt("totalrecovered");
            text = arr.getJSONObject(i).getString("date");
            date3 = LocalDate.parse(text,formatter);

            recoveredSeries.getData().add(new XYChart.Data<String, Number>(date3.toString(), recoveredSer));
            if(recoveredSeries.getData().size() > WINDOW_SIZE){
                recoveredSeries.getData().remove(0);
            }
        }

        ObservableList<XYChart.Series<String,Number>> fxlist = FXCollections.observableArrayList(series,deathSeries,recoveredSeries);
        covidData.setData(fxlist);

        for(final XYChart.Data<String, Number> data: series.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Tooltip infectedDataTooltp = new Tooltip("Total Infected :" + data.getYValue().toString() + "\n" + data.getXValue().toString());
                    infectedDataTooltp.getStyleClass().add("tpInfected");
                    Tooltip.install(data.getNode(), infectedDataTooltp);
                }
            });
        }

        for(final XYChart.Data<String, Number> deathData: deathSeries.getData()) {
            deathData.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Tooltip tooltip = new Tooltip("Total Deaths :" + deathData.getYValue().toString() + "\n" + deathData.getXValue().toString());
                    tooltip.getStyleClass().add("tpDeath");
                    Tooltip.install(deathData.getNode(), tooltip);

                }
            });
        }

        for(final XYChart.Data<String, Number> recovereddata: recoveredSeries.getData()) {
            recovereddata.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Tooltip tooltip = new Tooltip("Total Deaths :" + recovereddata.getYValue().toString() + "\n" + recovereddata.getXValue().toString());
                    tooltip.getStyleClass().add("tpRecovered");
                    Tooltip.install(recovereddata.getNode(), tooltip);

                }
            });
        }


    }

    public void initialize() throws IOException, JSONException, ParseException {
        init();
    }

}