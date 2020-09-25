/*******************************************************************************
 * Copyright (C) 2020, exense GmbH
 *  
 * This file is part of STEP
 *  
 * STEP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * STEP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with STEP.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package step.examples.java;
import java.text.SimpleDateFormat;
import java.util.Date;

import step.handlers.javahandler.AbstractKeyword;
import step.handlers.javahandler.Keyword;

public class JavaKeywordExample extends AbstractKeyword {

	@Keyword(name="Current time")
	public void getTime() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat(input.getString("format", "dd.MM.yyyy HH:mm:ss"));
		Date date;
		output.startMeasure("Demo_Java_Clock_subMeasurement");
		date = new Date();
		output.stopMeasure();

		output.add("time", format.format(date));
	}
	
	@Keyword(name="Set date format", schema="{\"format\":\"string\"}")
	public void setDateFormat() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat(input.getString("format"));
		session.put("format", format);
	}
	
	@Keyword(name="Get time using previously defined format")
	public void getTimeUsingPreviouslyDefinedFormat() throws Exception {
		Object object = session.get("format");
		if(object==null || !(object instanceof SimpleDateFormat)) {
			throw new Exception("Unable to find 'format' in session. Please call the keyword 'Set date format' first");
		}
		
		output.add("time", ((SimpleDateFormat)object).format(new Date()));
	}
	
	
}