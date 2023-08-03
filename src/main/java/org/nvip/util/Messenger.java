/**
 * Copyright 2023 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RSAT19CB0000020 awarded by the United
 * States Department of Homeland Security.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.nvip.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONArray;

public class Messenger {
	private static Logger logger = LogManager.getLogger(Messenger.class);

	public static void sendCveId(String cveId) {
        JSONArray cveArray = new JSONArray();
        cveArray.put(cveId);
        String mqHost = (System.getenv("MQ_HOST") == null) ? "localhost" : System.getenv("MQ_HOST");
        int mqPort = (System.getenv("MQ_PORT") == null) ? 5672 : Integer.parseInt(System.getenv("MQ_PORT"));

        try {
            // Create a connection to the RabbitMQ server and create the channel
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(mqHost);
            factory.setPort((int) mqPort);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            // Declare a queue and send the message
            String queueName = "CRAWLER_OUT";
            channel.queueDeclare(queueName, false, false, false, null);
            logger.info("Queue '{}' created successfully.", queueName);
            channel.basicPublish("", queueName, null, cveArray.toString().getBytes());
            logger.info("Message to Reconciler sent successfully.");

            channel.close();
            connection.close();
        } catch (Exception ex) {
            logger.error("ERROR: Failed to send message to MQ server on {} via port {}", mqHost,
                    mqPort);
        }
    }
}