/*
 * *******************************************************************************
 *  * Copyright (c) 2018 Edgeworx, Inc.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Eclipse Public License v. 2.0 which is available at
 *  * http://www.eclipse.org/legal/epl-2.0
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *******************************************************************************
 *
 */

package org.eclipse.iofog.comsat.commandline;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import org.eclipse.iofog.comsat.ComSat;
import org.eclipse.iofog.comsat.InstanceUtils;
import org.eclipse.iofog.comsat.utils.Constants;

public class CommandLineParser {

	public String parse(String command, String params) throws Exception {
		if (command.equals("stop")) {
			synchronized (ComSat.exitLock) {
				ComSat.exitLock.notifyAll();
			}
			return "ComSat stopped... :)";
		}
		
//		if (command.equals("reboot")) {
//			Runtime.getRuntime().exec("shutdown -r");
//			return "Rebooting in a minute";
//		}
//		
		return showHelp();
	}
	
	public boolean localParser(String... params) {
		if (params == null || params.length == 0 || params[0].equals("-h") || params[0].equals("help") || 
				params[0].equals("-?") || params[0].equals("--help")) {
			System.out.println(showHelp());
			return true;
		}
		
		if (params[0].equals("-v") || params[0].equals("--version")) {
			System.out.println(showVersion());
			return true;
		}
		
		String command = params[0];
		JsonObject response = isAnotherInstanceRunning(command, "");
		
		if (command.equals("start") && response != null) {
			System.out.println("ComSat is already running!");
			return true;
		} else if (command.equals("stop")) {
			if (response == null)
				System.out.println("ComSat is not running!");
			else
				System.out.println("Stopping ComSat...");
			return true;
		}
		
		return false;
	}
	
	private String showHelp() {
		return "Help!";
	}

	private String showVersion() {
		return "Version " + Constants.VERSION;
	}

	private JsonObject isAnotherInstanceRunning(String command, String param) {
        InstanceUtils instanceUtils = new InstanceUtils();
        Map<String, String> params = new HashMap<>();
        params.put("command", command);
        params.put("params", param);
        try {
        	return instanceUtils.sendHttpRequest("https://localhost" + Constants.API_COMMAND_LINE, params);
        } catch (Exception e) {
        	return null;
        }
	}

}