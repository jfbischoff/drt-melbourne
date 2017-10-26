/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
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

package melbourne.drt;

import java.util.LinkedHashSet;
import java.util.Set;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.drt.run.DrtConfigConsistencyChecker;
import org.matsim.contrib.drt.run.DrtConfigGroup;
import org.matsim.contrib.drt.run.DrtControlerCreator;
import org.matsim.contrib.dvrp.run.DvrpConfigGroup;
import org.matsim.contrib.otfvis.OTFVisLiveModule;
import org.matsim.core.config.*;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.TypicalDurationScoreComputation;
import org.matsim.core.config.groups.QSimConfigGroup.SnapshotStyle;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.VspExperimentalConfigGroup.VspDefaultsCheckingLevel;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vis.otfvis.OTFVisConfigGroup;

/**
 * @author jbischoff
 */
public class KNRunDrtMelbourne {

	public static void main(String[] args) {

		Config config = ConfigUtils.loadConfig( System.getProperty("user.home") + "/Dropbox/melbourne-berlin/scenario/drt-scenario/input/config-kai.xml", new DrtConfigGroup(), new DvrpConfigGroup(), new OTFVisConfigGroup());
		config.addConfigConsistencyChecker(new DrtConfigConsistencyChecker());

		//		config.controler().setFirstIteration(9);
		//		config.controler().setLastIteration(9);

		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		config.controler().setOutputDirectory( System.getProperty("user.home") + "/output" );

		config.planCalcScore().setBrainExpBeta(1.);
		for ( ActivityParams params : config.planCalcScore().getActivityParams() ) {
			params.setTypicalDurationScoreComputation( TypicalDurationScoreComputation.relative );
		}
		config.planCalcScore().setFractionOfIterationsToStartScoreMSA(0.8);

		config.qsim().setEndTime(36*24*3600.);
		config.qsim().setTrafficDynamics(TrafficDynamics.kinematicWaves);
		config.qsim().setSnapshotStyle(SnapshotStyle.kinematicWaves);
		config.qsim().setUsingFastCapacityUpdate(true);
		config.qsim().setUsingTravelTimeCheckInTeleportation(true) ;

		config.plans().setRemovingUnneccessaryPlanAttributes(true);

		config.vspExperimental().setVspDefaultsCheckingLevel(VspDefaultsCheckingLevel.warn);

		boolean otfvis = false ;
		{		
//			OTFVisConfigGroup otfvisconfig = ConfigUtils.addOrGetModule(config, OTFVisConfigGroup.class ) ;
//			otfvisconfig.setAgentSize(150);
//			otfvisconfig.setDrawTransitFacilities(false);
//			otfvis = true ;
		}
		config.checkConsistency();

		// ---

		Scenario scenario = ScenarioUtils.loadScenario(config) ;

//		for ( Link link : scenario.getNetwork().getLinks().values() ) {
//			Set<String> modes = new LinkedHashSet<>() ;
//			modes.addAll( link.getAllowedModes() ) ;
//			modes.add( DrtConfigGroup.DRT_MODE ) ;
//			link.setAllowedModes(modes);
//		}


		// ---

		Controler controler = DrtControlerCreator.createControler(scenario, otfvis);

		controler.run();

	}
}
