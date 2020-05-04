/* *********************************************************************** *
 * project: org.matsim.*												   *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2008 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */
package org.matsim.project;

import com.google.inject.internal.cglib.proxy.$Callback;
import com.graphhopper.jsprit.core.problem.Capacity;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.*;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nagel
 *
 */
public class RunMatsim{

	public static void main(String[] args) {

		Config config;
		if ( args==null || args.length==0 || args[0]==null ){
			config = ConfigUtils.loadConfig( "scenarios/equil/config.xml" );
		} else {
			config = ConfigUtils.loadConfig( args );
		}
		config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );


		// Config config = ConfigUtils.loadConfig( args ) ;

		// possibly modify config here

		// ---

		Scenario scenario = ScenarioUtils.loadScenario(config) ;
		// ll: Scenario : type/class, scenario: nom de la varialbe, scenarioUtils package loadscenario: fonction config Variable

		// possibly modify scenario here
		Id<Person> interestingPersonId = Id.createPersonId(1);
		// id class <Person> interface Id<Person> objet Id interesting ID class createpersonId fonction
		List<Id<Person>> personToRemove = new ArrayList<>();

		//une variable qui va parcourt toute la liste, variable locale; supprimé après
		for (Id<Person> personId : scenario.getPopulation().getPersons().keySet()) {
			if (!personId.equals(interestingPersonId)){
				personToRemove.add(personId);
			}
		}

		for (Id<Person> personId : personToRemove) {
			scenario.getPopulation().removePerson(personId);
		}

		//change link capacities
		/*Id<Link> interestingLinkId = Id.createLinkId(1);

		List<Id<Link>> linkToChange = new ArrayList<>();

		for (Id<Link> linkId : scenario.getNetwork().getLinks().keySet()) {
			if (!linkId.equals(interestingLinkId)){
				linkToChange.add(linkId);
			}

		for (Id<Link> linkId : linkToChange) {
			scenario.getNetwork().getLinks().get(linkId).setCapacity();
		}*/

		//méthode, objet population, get objet Person;
		System.out.println("Population size = " + scenario.getPopulation().getPersons().size());
		// ---

		//récupérer un générateur de fonction, un objet , récupérer l'usine dans le scénario pour
		PopulationFactory populationFactory = scenario.getPopulation().getFactory();

		Person person2 = populationFactory.createPerson(Id.createPersonId("Dominik"));

		Plan plan = populationFactory.createPlan();

		Activity homeActivity = populationFactory.createActivityFromLinkId( "h", Id.createLinkId(21));
		homeActivity.setEndTime(6*60*60.);
		plan.addActivity(homeActivity);

		// leg est un objet dans la classe TransportMode
		Leg leg = populationFactory.createLeg(TransportMode.car);
		plan.addLeg(leg);

		Activity workActivity = populationFactory.createActivityFromLinkId( "w", Id.createLinkId(1));
		plan.addActivity(workActivity);

		person2.addPlan(plan);

		scenario.getPopulation().addPerson(person2);

		System.out.println("Population size = " + scenario.getPopulation().getPersons().size());




		//Controler controler = new Controler( scenario ) ;

		Controler controler = new Controler( scenario ) ;
		controler.addOverridingModule(new OTFVisLiveModule() ) ;
		controler.run();


		// possibly modify controler here

//		controler.addOverridingModule( new OTFVisLiveModule() ) ;

		// ---

		controler.run();
	}

}
