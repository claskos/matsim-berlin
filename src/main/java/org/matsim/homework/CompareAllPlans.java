package org.matsim.analysis.homework;

import org.matsim.api.core.v01.population.Person;
import org.matsim.core.population.PopulationUtils;

import java.util.HashSet;
import java.util.Set;


/*
This class compares two plan files and returns all persons whose selected plans change between the two files.
 */
public class CompareAllPlans {

    // Retrieve all persons with changes in their selected plans
    public static Set<Person> getAffectedPersons(String plansFileNamePrev, String plansFileNameAfter) {

        // Read both populations
        var populationPrev = PopulationUtils.readPopulation(plansFileNamePrev);
        var populationAfter = PopulationUtils.readPopulation(plansFileNameAfter);
        Set<Person> affectedPersons = new HashSet<>();

        for (Person personPrev : populationPrev.getPersons().values()) {
            // Retrieve the plans from the same person (by ID) from both plan files
            var personAfter = populationAfter.getPersons().get(personPrev.getId());
            var planPrev = personPrev.getSelectedPlan();
            var planAfter = personAfter.getSelectedPlan();

            // For each person compare all plan elements
            if (personPrev.getId().equals(personAfter.getId())) {
                //System.out.println("Person #" + personPrev.getId());

                // Retrieve all legs of both plans
                var legsPrev = planPrev.getPlanElements();
                var legsAfter = planAfter.getPlanElements();

                // Compare all leg-pairs of the current person
                for (int i = 0; i < legsPrev.size(); i++) {
                    var legA = legsPrev.get(i);
                    var legB = legsAfter.get(i);

                    // If any of the legs diverge from each other add the person as an affected person
                    boolean b = legA.toString().equals(legB.toString());
                    if (!b) {
                        System.out.println("Added person #" + personPrev.getId());
                        affectedPersons.add(personPrev);
                        i = legsPrev.size();
                    }
                }
            }
        }
        // Return a list of all persons with a change in plans
        return affectedPersons;
    }


    public static void main(String[] args) {

        String plansFileNamePrev = null;
        String plansFileNameAfter = null;

        if (args.length > 1) {
            if (!args[0].equals("null")) plansFileNamePrev = args[0];
            if (!args[1].equals("null")) plansFileNameAfter = args[1];
        }
        else {
            // Feedback if no two plan file names were given
            System.out.println("CompareAllPlans was called without sufficient arguments. Using default values.");

            // Default values for testing purposes
            plansFileNamePrev = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/berlin-v5.5-1pct.output_plans.xml.gz";
            plansFileNameAfter = "/home/asdf/IdeaProjects/matsim-berlin/scenarios/berlin-v5.5-1pct/berlin-v5.5-1pct.output_plansTest.xml.gz";
        }

        // Check how many persons are affected by changes in their plans
        var affected = CompareAllPlans.getAffectedPersons(plansFileNamePrev, plansFileNameAfter);
        System.out.println("Number of persons affected by plan changes: " + affected.size());
    }


}
