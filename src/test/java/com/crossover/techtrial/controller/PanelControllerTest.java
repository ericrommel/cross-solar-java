package com.crossover.techtrial.controller;

import com.crossover.techtrial.model.HourlyElectricity;
import com.crossover.techtrial.model.Panel;
import com.crossover.techtrial.repository.PanelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * PanelControllerTest class will test all APIs in PanelController.java.
 * @author Crossover
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PanelControllerTest {
  
  MockMvc mockMvc;
  
  @Mock
  private PanelController panelController;
  
  @Autowired
  private TestRestTemplate template;

  @Autowired
  private PanelRepository panelRepository;

  @LocalServerPort
  private int port;

  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(panelController).build();
  }

  @Test
  public void testPanelShouldBeRegistered() throws Exception {
    HttpEntity<Object> panel = getHttpEntity(
        "{\"serial\": " + generateSerialNumber() + ", \"longitude\": \"54.123232\","
            + " \"latitude\": \"54.123232\",\"brand\":\"tesla\" }");
    ResponseEntity<Panel> response = template.postForEntity(
        "/api/register", panel, Panel.class);
    Assert.assertEquals(202,response.getStatusCode().value());
  }

  // TODO:  Check javax.persistence.NonUniqueResultException: query did not return a unique result. Maybe this is a Frontend concern.
  @Test
  public void testSaveHourlyElectricity() throws Exception {

      // Adding a panel
      String serialNumber = generateSerialNumber();

      HttpEntity<Object> panel = getHttpEntity(
              "{\"serial\": \"" + serialNumber + "\", \"longitude\": \"47.498012\","
                      + " \"latitude\": \"19.039912\",\"brand\":\"tesla\" }");
      ResponseEntity<Panel> responsePanel = template.postForEntity(
              "/api/register", panel, Panel.class);

      Assert.assertEquals(202,responsePanel.getStatusCode().value());

      // Get the panel added before
      Panel p = panelRepository.findBySerial(serialNumber);

      // Save hourly by a whole day and assert it
      Random random = new Random();
      for (int i=0; i < 24; i++) {
          HttpEntity<Object> hourlyElectricity = getHttpEntity(
                  "{\"panelId\": \"" + p.getId() + "\", \"generatedElectricity\": \"" +
                          random.nextInt(1001)+ "\", \"readingAt\": \"2018-08-15T"
                          + String.format("%02d", i) + ":00:00\"}");

          ResponseEntity<HourlyElectricity> responseHourly = template.postForEntity(
                  "/api/panels/" + serialNumber + "/hourly", hourlyElectricity, HourlyElectricity.class);
          Assert.assertEquals(200,responseHourly.getStatusCode().value());
      }
  }

  @Test
  public void testHourlyElectricity() {
      // Getting a null panel (not existent)
      ResponseEntity<Object[]> res404 = template.getForEntity(
              "/api/panels/" + "NULL123456789123" + "/hourly", Object[].class
      );

      Assert.assertEquals(404,res404.getStatusCode().value());

      // Create a panel
      String serialNumber = generateSerialNumber();
      HttpEntity<Object> panel = getHttpEntity(
              "{\"serial\": \"" + serialNumber + "\", \"longitude\": \"64.217012\","
                      + " \"latitude\": \"11.229812\",\"brand\":\"tesla\" }");
      ResponseEntity<Panel> responsePanel = template.postForEntity(
              "/api/register", panel, Panel.class);

      Assert.assertEquals(202,responsePanel.getStatusCode().value());

      // Save hourly (3 hour points) for 2 days
      String panelId = panelRepository.findBySerial(serialNumber).getId().toString();
      for (int i=10; i < 12; i++) {
          for (int j=7; j<10; j++) {
              HttpEntity<Object> hourlyElectricity = getHttpEntity(
                      "{\"panelId\": \"" + panelId + "\", \"generatedElectricity\": \"" + (i + j) + "\"," +
                              " \"readingAt\": \"2018-08-" + i + "T"
                              + String.format("%02d", j) + ":00:00\"}");

              ResponseEntity<HourlyElectricity> responseHourly = template.postForEntity(
                      "/api/panels/" + serialNumber + "/hourly", hourlyElectricity, HourlyElectricity.class);
              Assert.assertEquals(200,responseHourly.getStatusCode().value());
          }
      }

      // Getting the panel's id by serial number
      ResponseEntity<Object> response = template.getForEntity(
              "/api/panels/" + serialNumber + "/hourly", Object.class
      );

      Assert.assertEquals(200,response.getStatusCode().value());

  }

  @Test
  public void testGetAllPanel() throws Exception {
      List<Panel> allPanels = new ArrayList<>();

      Panel panel1 = new Panel();
      panel1.setSerial(generateSerialNumber());
      panel1.setLatitude(47.498010);
      panel1.setLongitude(19.039910);
      panel1.setBrand("Tesla");

      allPanels.add(panel1);

      Panel panel2 = new Panel();
      panel2.setSerial(generateSerialNumber());
      panel2.setLatitude(-23.533773);
      panel2.setLongitude(-46.625290);
      panel2.setBrand("Tesla");
      allPanels.add(panel2);

      given(panelController.getAllPanels()).willReturn(allPanels);

      mockMvc.perform(get("/api/panels")
              .contentType(APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(2)))
              .andExpect(jsonPath("$[0].serial", is(panel1.getSerial())))
              .andExpect(jsonPath("$[1].serial", is(panel2.getSerial())));
  }

  @Test
  public void testAllDailyElectricityFromYesterday() {
      /* Getting a null panel (not existent) */
      ResponseEntity<Object[]> res404 = template.getForEntity(
              "/api/panels/" + "NULL123456789123" + "/daily", Object[].class
      );
      Assert.assertEquals(404,res404.getStatusCode().value());

      /* Create a panel */
      String serialNumber = generateSerialNumber();
      HttpEntity<Object> panel = getHttpEntity(
              "{\"serial\": \"" + serialNumber + "\", \"longitude\": \"64.217012\","
                      + " \"latitude\": \"11.229812\",\"brand\":\"tesla\" }");
      ResponseEntity<Panel> responsePanel = template.postForEntity(
              "/api/register", panel, Panel.class);
      Assert.assertEquals(202,responsePanel.getStatusCode().value());

      String panelId = panelRepository.findBySerial(serialNumber).getId().toString();

      /* Save hourly (3 hour points) for TODAY */
      for (int i=7; i<10; i++) {
          HttpEntity<Object> hourlyElectricity = getHttpEntity(
                  "{\"panelId\": \"" + panelId + "\", \"generatedElectricity\": \"" + 2 * (i + i) + "\"," +
                          " \"readingAt\": \"" + LocalDate.now() + "T"
                          + String.format("%02d", i) + ":00:00\"}");

          ResponseEntity<HourlyElectricity> responseHourly = template.postForEntity(
                  "/api/panels/" + serialNumber + "/hourly", hourlyElectricity, HourlyElectricity.class);
          Assert.assertEquals(200, responseHourly.getStatusCode().value());
      }

      /* Getting the panel's id by serial number */
      ResponseEntity<Object[]> responseWithNoResults = template.getForEntity(
              "/api/panels/" + serialNumber + "/daily", Object[].class
      );
      System.out.println("RESULT CODE: " + responseWithNoResults.getStatusCodeValue());
      Assert.assertEquals(204,responseWithNoResults.getStatusCode().value());

      /* Save hourly (3 hour points) for 2 days */
      for (int i=10; i < 12; i++) {
          for (int j=7; j<10; j++) {
              HttpEntity<Object> hourlyElectricity = getHttpEntity(
                      "{\"panelId\": \"" + panelId + "\", \"generatedElectricity\": \"" + 2*(i + j) + "\"," +
                              " \"readingAt\": \"2018-08-" + i + "T"
                              + String.format("%02d", j) + ":00:00\"}");

              ResponseEntity<HourlyElectricity> responseHourly = template.postForEntity(
                      "/api/panels/" + serialNumber + "/hourly", hourlyElectricity, HourlyElectricity.class);
              Assert.assertEquals(200,responseHourly.getStatusCode().value());
              /*
               * Generated Data (DailyElectricity):
               * [date=2018-08-10, time01=07:00:00 (gen=34), time02=08:00:00 (gen=36), time03=09:00:00 (gen=38)]
               * [date=2018-08-11, time01=07:00:00 (gen=36), time02=08:00:00 (gen=38), time03=09:00:00 (gen=40)]
               * */
          }
      }

      /*
       * Daily Results to check:
       * DailyElectricity [date=2018-08-10, sum=108, average=36.0, min=34, max=38]
       * DailyElectricity [date=2018-08-11, sum=114, average=38.0, min=36, max=40]
       * */
      /* Getting the panel's daily by serial number */
      ResponseEntity<Object[]> resDaily = template.getForEntity(
              "/api/panels/" + serialNumber + "/daily", Object[].class
      );

      Assert.assertEquals(200,resDaily.getStatusCode().value());

      /* Check results on response body and assert it */
      ObjectMapper oMapper = new ObjectMapper();
      @SuppressWarnings (value="unchecked")
      List<Map<?, ?>> objBodies = oMapper.convertValue(resDaily.getBody(), List.class);
      for (Map<?, ?> obj : objBodies) {
          // {date=2018-08-11, sum=114, average=38.0, min=36, max=40}
          if (obj.get("date").equals("2018-08-11")) {
              Assert.assertEquals(114, obj.get("sum"));
              Assert.assertEquals(38.0, obj.get("average"));
              Assert.assertEquals(36, obj.get("min"));
              Assert.assertEquals(40, obj.get("max"));
          }
          // {date=2018-08-10, sum=108, average=36.0, min=34, max=38}
          if (obj.get("date").equals("2018-08-10")) {
              Assert.assertEquals(108, obj.get("sum"));
              Assert.assertEquals(36.0, obj.get("average"));
              Assert.assertEquals(34, obj.get("min"));
              Assert.assertEquals(38, obj.get("max"));
          }
      }
  }

  private HttpEntity<Object> getHttpEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<Object>(body, headers);
  }

  private String generateSerialNumber() {
      StringBuilder stringBuilder = new StringBuilder();
      Random random = new Random();
      String subset = "0123456789";
      for (int i = 0; i < 16; i++) {
          int index = random.nextInt(subset.length());
          char c = subset.charAt(index);
          stringBuilder.append( c );
      }
      return stringBuilder.toString();
  }
}
