package ch.exense.monitoring.managedprocess;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import step.commons.processmanager.ManagedProcess.ManagedProcessException;
import step.expressions.ExpressionHandler;
import step.handlers.javahandler.Keyword;

public class TypePerfManagedProcessKeywords extends ManagedProcessKeywords {
	
	List<Metric> metrics;
	ExpressionHandler expressionHandler;
	String hostname;
	
	public TypePerfManagedProcessKeywords() {
		metrics = new ArrayList<Metric>();
		expressionHandler = new ExpressionHandler();
		hostname = getLocalHostname();
	}
	
	
	@Keyword
	public void TypePerfManagedProcessKeyword() throws IOException, ManagedProcessException{
		  
		getManagedProcessMandatoryInput();
		
		String cmd = buildCommandLine();
		
		executeManagedCommand(cmd);
	}
	
	private String createTypePerfArgs() throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = this.getClass().getResourceAsStream("/typePerf.csv");
		BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while (( line = bf.readLine()) != null) {			
			if (line.startsWith("Metric_name,Typeperf_counter")) {
				continue;
			}
			String[] columns = line.split(",");
			String expression = (columns.length > 2) ? columns[2] : "";
			metrics.add(new Metric(columns[0],columns[1],expression));
			sb.append(" \"").append(columns[1]).append("\"");
		}
		sb.append(" -sc 1");
		if (input.containsKey("hostname")) {
			sb.append(" -s ").append(input.getString("hostname"));
		}
		return sb.toString();
	}

	@Override
	protected void executionPostProcess(File file) throws IOException {
		//Extract the output line with values ex: "04/04/2019 14:05:07.730","79.863592","9181.000000"
		String line = Files.readAllLines(file.toPath(), Charset.defaultCharset()).get(2).replaceAll("\"", "");
		String[] values = line.split(",");		
		JsonArrayBuilder measurementArrayBuilder = Json.createArrayBuilder();
		JsonObjectBuilder measurementBuilder = Json.createObjectBuilder();		
		Map<String, Object> bindings = new HashMap<String, Object> ();
		
		if (metrics.size() == (values.length-1)) {
			String ts = values[0];
			for (int i=1; i < values.length;i++) {
				
				long value = Math.round(Double.parseDouble(values[i]));
				if (!metrics.get(i-1).getGroovyExpression().isEmpty()) {
					bindings.put("value",value);					
					Object gResult = expressionHandler.evaluateGroovyExpression(metrics.get(i-1).getGroovyExpression(), bindings);
					value= (long) gResult;
				}						
				long thisTime = System.currentTimeMillis();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("eId", "OSmonitor");
				map.put("hostname", hostname);
				output.addMeasure(metrics.get(i-1).getName(), value, map);
				measurementBuilder.add("begin", thisTime)
					.add("eId", "OSmonitor")
					.add("value", value)
					.add("name", metrics.get(i-1).getName())
					.add("hostname", hostname);
				measurementArrayBuilder.add(measurementBuilder.build());
			}
			output.add("measurements", measurementArrayBuilder.build().toString());
		}
	}
	
	@Override
	protected String buildCommandLine() throws IOException {
		return new StringBuilder()
			.append(executablePath)
			.append(" ")
			.append(createTypePerfArgs()).toString();
		
	}
	
	protected String getLocalHostname() {
		String hostname=null;
        try {
        	InetAddress ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            
        } catch (UnknownHostException e) {
 
            e.printStackTrace();
        } 
        return hostname;
	}
	
	public class Metric {
		private String name;
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCounter() {
			return counter;
		}

		public void setCounter(String counter) {
			this.counter = counter;
		}

		public String getGroovyExpression() {
			return groovyExpression;
		}

		public void setGroovyExpression(String groovyExpression) {
			this.groovyExpression = groovyExpression;
		}

		public Metric(String name, String counter, String groovyExpression) {
			super();
			this.name = name;
			this.counter = counter;
			this.groovyExpression = groovyExpression;
		}

		private String counter;
		private String groovyExpression;
		
		
	}
}
