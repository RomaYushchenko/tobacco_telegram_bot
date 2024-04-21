package com.ua.yushchenko.tabakabot.common.setup;

import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.csv.CSVFormat.*;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;
import com.ua.yushchenko.tabakabot.model.domain.Item;
import com.ua.yushchenko.tabakabot.model.domain.Order;
import com.ua.yushchenko.tabakabot.model.enums.ItemType;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVHelper {

    public static List<Item> csvToItems(final InputStream inputStream) {
        try (final BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
             final CSVParser csvParser = new CSVParser(fileReader,
                                                       DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase()
                                                              .withTrim())) {

            final List<Item> items = new ArrayList<>();

            final Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            long itemId = 1;

            for (CSVRecord csvRecord : csvRecords) {

                final Item tobaccoItem = Item.builder()
                                             .itemId(itemId)
                                             .itemType(ItemType.getEnumByString(csvRecord.get("tobaccoType")))
                                             .description(csvRecord.get("taste"))
                                             .weight(Integer.parseInt(csvRecord.get("weight")))
                                             .build();


                items.add(tobaccoItem);

                itemId++;
            }

            return items;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static void itemsToCsv(final List<Item> items, final String file){
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            // Write header
            String[] header = {"tobaccoType", "taste", "weight"}; // Replace with your actual column names
            writer.writeNext(header);

            items.forEach(item -> {
                String[] row = {
                        String.valueOf(item.getItemType().getItemString()),
                        String.valueOf(item.getDescription()),
                        String.valueOf(item.getWeight())
                };
                writer.writeNext(row);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void orderToCsv(final List<Order> orders, final String file){
        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            // Write header
            String[] header = {"orderId", "userId", "tobaccoItemId", "orderTime", "orderStatus"};
            writer.writeNext(header);

            orders.forEach(order -> {
                String[] row = {
                        String.valueOf(order.getOrderId()),
                        String.valueOf(order.getUserId()),
                        String.valueOf(order.getTobaccoItemId()),
                        String.valueOf(order.getOrderTime()),
                        String.valueOf(order.getOrderStatus().toString())
                };
                writer.writeNext(row);
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
