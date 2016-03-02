package hibernate.weather.sample;

/**
 * Created by tim_barrett on 2/16/2016.
 */
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.*;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tim_barrett on 9/2015.
 */
public class restClientFunc {
    /**
     * Method deletes records from the database
     *
     * @param delString
     * @return
     */
    public int restDelete(String delString) {
        int statusCode = 999;
        DeleteMethod delMethod = new DeleteMethod(delString);
        HttpClient httpClient = new HttpClient();
        try {
            statusCode = httpClient.executeMethod(delMethod);
            System.out.println("Delete day range status = " + statusCode);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            delMethod.releaseConnection();
        }
        return statusCode;
    }

    /**
     * Rest command to add records to the couchDb
     *
     * @param postString
     * @return
     */
    public int restPost (String postString){
        System.out.println("restPost method");
        int statusCode = 999;
        PostMethod postMethod = new PostMethod("http://127.0.0.1:5984/weather/");
        try {
            StringRequestEntity requestEntity = new StringRequestEntity(
                    postString,
                    "application/json",
                    "UTF-8");

            postMethod.setRequestEntity(requestEntity);

            HttpClient httpClient = new HttpClient();
            statusCode = httpClient.executeMethod(postMethod);
        } catch (UnsupportedEncodingException u) {
            System.out.println("populateDb2 Exception " + u.getMessage());
        } catch (IOException e) {
            System.out.println("populate DB2 IO exception" + e.getMessage());
        }
        finally {
            postMethod.releaseConnection();
        }
        return statusCode;
    }

    /**
     * rest command to get records from the couchDb
     *
     * @param getString
     * @return
     */
    public ArrayList<String> restGet (String getString){
        System.out.println("restGet Method " + getString);
        GetMethod getMethod = new GetMethod(getString);
        String readLine;
        HttpClient httpClient = new HttpClient();
        List<String> getResult = new ArrayList<String>();

        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("ERROR with Rest Get Command - " + getMethod.getResponseBodyAsString());
            } else {
                System.out.println("Get status code = " + statusCode);
                BufferedReader br = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream()));

                while ((readLine = br.readLine()) != null) {
                    System.out.println("READLINE = " + readLine);
                    getResult.add(readLine.toString());
                }

            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            getMethod.releaseConnection();
        }
        return (ArrayList<String>) getResult;
    }

    /**
     * rest based put used to create the database
     * couchDb requires a Put for database creation
     *
     * @param postString
     * @return
     */
    public int restPut(String postString){
        HttpClient httpClient = new HttpClient();
        PutMethod putMethod = new PutMethod(postString);
        int created = 0;
        try {
            int statusCode = httpClient.executeMethod(putMethod);
            if (statusCode == HttpStatus.SC_OK){
                created = 1;
            }
            System.out.println("Return status - DB write = " + statusCode);
        } catch (UnsupportedEncodingException u) {
            System.out.println("populateDb2 Exception " + u.getMessage());
        } catch (IOException e) {
            System.out.println("populate DB2 IO exception" + e.getMessage());
        }
        finally {
            putMethod.releaseConnection();
            return created;
        }
    }

}

