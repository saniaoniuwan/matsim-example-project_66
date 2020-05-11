package org.matsim.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.ControlerConfigGroup;
import org.matsim.core.config.groups.QSimConfigGroup;
import org.matsim.core.config.groups.StrategyConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;

public class NewMatsimRunner {

    public static void main(String args){
/*
        Config config = ConfigUtils.createConfig();
*/
        Config config = ConfigUtils.loadConfig("scenario/equil/config.xml");
        config.controler().setLastIteration(5);
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);

        Scenario scenario = ScenarioUtils.loadScenario(config);
        /*using another input file, or take the output_config.xml to show where it's picked up, */
        config.plans().setInputFile("../../output/output_plans100.xml.gz");
        config.network().setInputFile("../../output/output_network.xml.gz");
        config.controler().setWriteEventsInterval(100);
        config.controler().setRoutingAlgorithmType(ControlerConfigGroup.RoutingAlgorithmType.FastAStarLandmarks);

        /*multi-thread computing? */
        config.global().setNumberOfThreads(5); //replanning , 5 routers, everything
        config.parallelEventHandling().setNumberOfThreads(3); //in general, better do not touch this!
        config.qsim().setNumberOfThreads(2); //We have to try the qsim thread number out, usually smaller than global Nb of thread

        config.global().setCoordinateSystem("EPSG:xxxx");

        config.qsim().setTrafficDynamics(QSimConfigGroup.TrafficDynamics.kinematicWaves); //default
        config.qsim().setStartTime(100); //should be a small number

        StrategyConfigGroup.StrategySettings stratSets = new StrategyConfigGroup.StrategySettings();
        stratSets.setWeight(0.1);
        stratSets.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeTripMode); //
        stratSets.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ChangeSingleTripMode);
        stratSets.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta);//in output_config; comments are in red, then we could see options

        config.strategy().addStrategySettings(stratSets);
        String[] modes = {"car","bike"};//take the initial plan as genetic
        config.changeMode().setModes(modes);//check in output_plans, if defined modes are used

        config.strategy().clearStrategySettings();//in this case, clear all strategy settings; run; find the first error that appears
        {
            StrategyConfigGroup.StrategySettings stratSettings = new StrategyConfigGroup.StrategySettings();
            stratSettings.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.BestScore); //balance, experation/exploration;
            stratSets.setWeight(1.);
            config.strategy().addStrategySettings(stratSettings);
            /*Scenario scenario = ScenarioUtils.loadScenario(config);*/
        }

        {
            StrategyConfigGroup.StrategySettings stratSettings = new StrategyConfigGroup.StrategySettings();
            stratSettings.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute);
            stratSets.setWeight(0.1);
            config.strategy().addStrategySettings(stratSettings);
/*
            Scenario scenario = ScenarioUtils.loadScenario(config);
*/
        }

/*        config.travelTimeCalculator();
        config.linkStats();*/

        /*You should not put the Input files after the Scenario section! Too late*/
        Controler controler = new Controler(scenario);

        /*you could look into the gz without dezip*/

        controler.run();
    }
}
