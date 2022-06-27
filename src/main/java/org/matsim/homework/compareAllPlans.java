package org.matsim.homework;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.population.PopulationUtils;

import java.util.HashSet;
import java.util.Set;


// Reads a shape file (.shp) and returns the point coordinates for a single specified shape
// Check the attribute and ID key-value pair in Via
public class compareAllPlans {

    public static Set<Person> getAffectedPersons(String plansFileNamePrev, String plansFileNameAfter) {


        var populationPrev = PopulationUtils.readPopulation(plansFileNamePrev);
        var populationAfter = PopulationUtils.readPopulation(plansFileNameAfter);
        Set<Person> affectedPersons = new HashSet<>();

        for (Person personPrev : populationPrev.getPersons().values()) {
            // Compare the plans from the same person (ID) from both plan files
            var personAfter = populationAfter.getPersons().get(personPrev.getId());
            var planPrev = personPrev.getSelectedPlan();
            var planAfter = personAfter.getSelectedPlan();

            // If the plans diverge from each other add the person as an affected person
            if (personPrev.getId().equals(personAfter.getId()) && !planPrev.equals(planAfter)) {
                affectedPersons.add(personPrev);
            }
        }

        // Return a list of all persons with a change in plans
        return affectedPersons;
    }


    public static void main(String[] args) {

        String plansFileNamePrev = null;
        String plansFileNameAfter = null;

        if (args.length > 0) {
            if (!args[0].equals("null")) plansFileNamePrev = args[0];
            if (!args[1].equals("null")) plansFileNameAfter = args[1];
        }
        else {
            //System.out.println("No plan file names given for comparison.");
            //return;
            plansFileNamePrev = "C:\\Users\\Nico\\Documents\\Nico\\Uni\\Module\\Multi-agent transport simulation\\output-berlin-v5.5-1pct\\berlin-v5.5-1pct.output_plans.xml.gz";
            plansFileNameAfter = "C:\\Users\\Nico\\Documents\\Nico\\Uni\\Module\\Multi-agent transport simulation\\output-berlin-v5.5-1pct\\berlin-v5.5-1pct.output_plansTest.xml.gz";
        }

        var affected = compareAllPlans.getAffectedPersons(plansFileNamePrev, plansFileNameAfter);

        System.out.println(affected.size());
    }


}
