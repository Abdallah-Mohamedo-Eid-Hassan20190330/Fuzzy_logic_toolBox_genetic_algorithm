package com.company;

import java.io.*;
import java.sql.Array;
import java.util.*;

public class Main {

    static class Fuzzy_toolbox
    {
        ArrayList<Variables> variables = new ArrayList<>();
        ArrayList<Rules> rules = new ArrayList<>()  ;
        String name , description ;
        Fuzzy_toolbox(String name , String description)
        {
            this.name = name ;
            this.description = description ;
        }
        void add_variable (String statement)
        {
            variables.add(new Variables(statement)) ;
        }
        void add_Rule (String statement)
        {
            rules.add(new Rules(statement , this)) ;
        }
        Variables get_variable(String name )
        {
            for ( Variables x : variables)
            {
                if (x.name.equals(name))
                    return x ;
            }
            return null;
        }

        double result ()
        {
            ArrayList<Double> membership = new ArrayList<>();
            ArrayList<Double> centroid = new ArrayList<>();
            double sum_degrees_of_membership = 0.0 ;
            double avg = 0.0 ;
            for (Variables x : variables)
            {
                if (x.mode.equals("OUT"))
                {
                    for (Fuzzy_sets y : x.sets)
                    {
                        membership.add(y.degree_of_membership) ;
                        centroid.add(y.centroid) ;
                        sum_degrees_of_membership += y.degree_of_membership ;
                    }
                }
            }
            for (int i = 0 ; i < membership.size() ; i++)
            {
                avg += membership.get(i) * centroid.get(i) ;
            }
            avg = avg / sum_degrees_of_membership ;
            membership.clear();
            centroid.clear();

            return avg ;
        }
    }
    static class Rules
    {
        String xValue ;
        String xFuzzy ;
        double xMembership ;
        String operator ;
        String yValue ;
        String yFuzzy ;
        double yMembership ;
        String zValue ;
        String zFuzzy ;
        double zMembership ;
        Fuzzy_toolbox logic ;

        Rules(String statement , Fuzzy_toolbox logic)
        {
            String rule[] = statement.split(" ") ;
            this.xValue = rule[0] ;
            this.xFuzzy = rule[1] ;
            this.operator = rule[2] ;
            this.yValue = rule[3] ;
            this.yFuzzy = rule[4] ;
            this.zValue = rule[6] ;
            this.zFuzzy = rule[7] ;
            this.logic = logic ;
        }
        void setzFuzzy ()
        {
            for (int i = 0 ; i < logic.variables.size() ; i ++ )
            {
                if (logic.variables.get(i).name.equals(xValue))
                {
                    for (int j = 0 ; j < logic.variables.get(i).sets.size() ; j++ )
                    {
                        if (logic.variables.get(i).sets.get(j).name.equals(xFuzzy))
                        {
                            this.xMembership = logic.variables.get(i).sets.get(j).degree_of_membership ;
                        }
                    }
                }
                else if (logic.variables.get(i).name.equals(yValue))
                {
                    for (int j = 0 ; j < logic.variables.get(i).sets.size() ; j ++ )
                    {
                        if (logic.variables.get(i).sets.get(j).name.equals(yFuzzy))
                        {
                            this.yMembership = logic.variables.get(i).sets.get(j).degree_of_membership ;
                        }
                    }
                }
            }
            if (operator.equals("and"))
            {
                this.zMembership = Math.min(xMembership , yMembership) ;
            }
            else if (operator.equals("or"))
            {
                this.zMembership = Math.max(xMembership , yMembership) ;
            }
            for (int i = 0 ; i < logic.variables.size() ; i ++)
            {
                if (logic.variables.get(i).mode.equals("OUT"))
                {
                    if (logic.variables.get(i).name.equals(zValue))
                    {
                        for (int j = 0 ; j < logic.variables.get(i).sets.size() ; j ++)
                        {
                            if (logic.variables.get(i).sets.get(j).name.equals(zFuzzy))
                            {
                                logic.variables.get(i).sets.get(j).degree_of_membership = this.zMembership ;
                            }
                        }
                    }
                }

            }
        }

        void print ()
        {
            System.out.println(xValue + " " + xFuzzy + " ");
            System.out.println(operator);
            System.out.println(yValue + " " + yFuzzy);
            System.out.println(zValue + " " + zFuzzy);

        }

    }

    static class Fuzzy_sets
    {
        String name ;
        String shape ;
        String range ;
        ArrayList<Double> vertices = new ArrayList<>();
        ArrayList<Double> vertices_y = new ArrayList<>();
        double degree_of_membership = 0 ;
        double centroid ;
        double crisp_value ;
        Fuzzy_sets(String statement )
        {
            String [] arr = statement.split(" ") ;

            this.name = arr[0] ;
            this.shape = arr[1] ;
            this.range = formate_set_range(arr) ;
            this.degree_of_membership = 0 ;
            String []splitter = range.split(" ") ;
            for (String x : splitter)
            {
                vertices.add(Double.parseDouble(x)) ;
            }
            this.centroid = centroid() ;
            this.vertices_y = getVertices_y() ;
        }
        Double centroid ()
        {
            if (shape.equals("TRI"))
            {
                return vertices.get(1) ;
            }
            else if (shape.equals("TRAP"))
            {
                if (vertices.get(0) == vertices.get(1))
                    return vertices.get(2) ;
                else if (vertices.get(2) == vertices.get(3))
                    return vertices.get(1) ;
                else
                {
                    double avg = 0 ;
                    for (double i : vertices)
                    {
                        avg += i ;
                    }
                    return avg / 4.0 ;
                }
            }
            return null;
        }
        ArrayList<Double> getVertices_y ()
        {
            ArrayList<Double> y = new ArrayList<>() ;
            if (shape.equals("TRI"))
            {
                y.add(0.0) ;
                y.add(1.0) ;
                y.add(0.0) ;
                return  y ;
            }
            y.add(0.0);
            y.add(1.0) ;
            y.add(1.0) ;
            y.add(0.0) ;
            return y ;
        }

        void calc_degree_of_membership (double crisp_value )
        {
            for (int i = 0 ; i < vertices.size()-1 ; i ++ )
            {
                double x1 = vertices.get(i) ;
                double y1 = vertices_y.get(i) ;
                double x2 = vertices.get(i+1);
                double y2 = vertices_y.get(i+1) ;
                if (x1 - x2 != 0 && crisp_value >=x1 && crisp_value <= x2 )
                {
                    double m = (y2 - y1 ) / (x2 - x1 ) ;
                    double c = y1 - m * x1 ;
                    this.degree_of_membership = m * crisp_value + c ;
                }
            }
        }

        void print()
        {
            System.out.println(name + " " + shape + " " + range );
        }
    }
    static class Variables
    {
        String name ;
        String mode ;
        int start_range ;
        int end_range ;
        ArrayList<Fuzzy_sets> sets = new ArrayList<>();
        int crisp_value ;

        Variables (String statement)
        {
            statement = formate_variable(statement) ;
            String [] arr = statement.split(" ") ;
            this.name = arr[0] ;
            this.mode = arr[1] ;
            this.start_range = Integer.parseInt(arr[2]) ;
            this.end_range = Integer.parseInt(arr[3]) ;
        }
        void add_set(String statement)
        {
            sets.add(new Fuzzy_sets(statement));
        }


        void print()
        {
            System.out.println(name + " " + mode + " " + "["  + start_range + "," + end_range + "]");
        }
    }


    public static String formate_range(String range)
    {
        String formated_range = "" ;
        String tmp_range[] = range.split("") ;
        for (int i = 0 ; i < range.length() ; i ++)
        {

            if (i == 0 || i == range.length() -1  )
            {

            }
            else
            {
                formated_range += tmp_range[i];
            }

        }
        return formated_range.trim() ;
    }
    public static String formate_variable (String variable )
    {
        String arr[] = variable.split(" ") ;
        String name = arr[0] ;
        String mode = arr[1] ;
        String range = arr[2] ;

        String formated_range = formate_range(range);
        String ranges[] = formated_range.split(",") ;
        String first_range = ranges[0] ;
        String second_range = ranges[1] ;

        String variable_data = name + " "+ mode + " " + first_range + " " + second_range ;
        return variable_data ;

    }

    void run (Variables [] variables , Rules rules )
    {

    }
    public static String formate_set_range(String set[])
    {

        String range = "" ;
        for (int i = 2 ; i < set.length ; i ++)
        {
            if (i == set.length -1)
                range += set[i] ;
            else
                range += set[i] + " " ;
        }
        return range ;
    }
    public static void main(String[] args) throws IOException {

//        System.out.println("Fuzzy logic toolbox");
//        System.out.println("===================");
//        String quite = "";
//        String name = "", description = "" , statement = "" ;
//        int choice;
//        Fuzzy_toolbox toolbox ;
//        boolean flag_var = false , flag_set = false , flag_rule = false ;
//        Scanner scan = new Scanner(System.in);
//        Scanner scanInt = new Scanner(System.in);
//        Scanner scanLine = new Scanner(System.in);
//        Scanner scanDouble = new Scanner(System.in) ;
//
//        while (true) {
//            System.out.println("1- create a new fuzzy system");
//            System.out.println("2- quite");
//            choice = scanInt.nextInt();
//            if (choice == 2) {
//                System.out.println("quitting...");
//                break;
//            }
//            else if (choice == 1) {
//                System.out.println("Enter the system’s name and a brief description:");
//                name = scanLine.nextLine();
//                description = scanLine.nextLine();
//                toolbox = new Fuzzy_toolbox(name , description) ;
//                while (true)
//                {
//                    System.out.println("Main Menu:");
//                    System.out.println("==========");
//                    System.out.println("1- Add variables.");
//                    System.out.println("2- Add fuzzy sets to an existing variable.");
//                    System.out.println("3- Add rules.");
//                    System.out.println("4- Run the simulation on crisp values.");
//                    System.out.println("5- back");
//                    choice = scanInt.nextInt();
//                    if (choice == 5)
//                    {
//                        System.out.println("returning...\n\n");
//                        break;
//                    }
//                    switch (choice)
//                    {
//                        case 1 :
//                            flag_var = true ;
//                            System.out.println("Enter the variable’s name, type (IN/OUT) and range ([lower, upper]):");
//                            System.out.println("(press x to finish)");
////                            statement = scan.nextLine() ;
////                            toolbox.add_variable(statement);
//                            while(true)
//                            {
//                                statement = scan.nextLine() ;
//                                if (statement.equals("x"))
//                                    break;
//                                toolbox.add_variable(statement);
//                            }
//                            break ;
//                        case 2 :
//                            if (flag_var) {
//                                flag_set = true;
//                                System.out.println("Enter the variable's name: ");
//                                System.out.println("------------------------------------------------------------");
//                                name = scan.next();
//                                System.out.println("Enter the fuzzy set name, type (TRI/TRAP) and values: (Press x to finish)");
//                                System.out.println("------------------------------------------------------------");
//                                while (true) {
//                                    statement = scanLine.nextLine();
//                                    if (statement.equals("x"))
//                                        break;
//                                    toolbox.get_variable(name).add_set(statement);
////                                    for (int i = 0; i < toolbox.variables.size(); i++)
////                                    {
////                                        if (toolbox.variables.get(i).name.equals(name))
////                                            toolbox.variables.get(i).add_set(statement);
////                                    }
//                                }
//                            }
//                            else
//                            {
//                                System.out.println("please enter variable first !! ");
//                            }
//                            break ;
//                        case 3 :
//                            if(flag_set && flag_var)
//                            {
//                                flag_rule = true ;
//                                System.out.println("Enter the rules in this format: (Press x to finish)");
//                                System.out.println("IN_variable set operator IN_variable set => OUT_variable set");
//                                System.out.println("------------------------------------------------------------");
//                                while(true)
//                                {
//                                    statement = scanLine.nextLine();
//                                    if (statement.equals("x"))
//                                        break ;
//                                    toolbox.add_Rule(statement);
//                                }
//                            }
//                            else
//                            {
//                                System.out.println("Please add first variable with it's fuzzy sets !!");
//                            }
//                            break ;
//                        case 4 :
//                            if (flag_rule && flag_set && flag_var)
//                            {
//                                System.out.println("Enter the crisp values:");
//                                System.out.println("-----------------------");
//                                for (int i = 0 ; i < toolbox.variables.size() ; i ++)
//                                {
//                                    if (toolbox.variables.get(i).mode.equals("IN"))
//                                    {
//                                        System.out.print(toolbox.variables.get(i).name + " :  ");
//                                        double crisp_value = scanDouble.nextDouble();
//                                        //toolbox.variables.get(i).crisp_value = crisp_value;
//                                        for (int j = 0; j < toolbox.variables.get(i).sets.size(); j++)
//                                        {
//                                            toolbox.variables.get(i).sets.get(j).calc_degree_of_membership(crisp_value);
//                                        }
//                                    }
//                                }
//
//                                for (Rules r : toolbox.rules)
//                                {
//                                    r.setzFuzzy();
//                                }
//
//                                double result = toolbox.result();
//                                System.out.println("Running the simulation…");
//                                System.out.println("Fuzzification => done");
//                                System.out.println("Inference => done");
//                                System.out.println("Defuzzification => done");
//
//                                System.out.println("the result is : " + result);
//
//                            }
//                            else
//                            {
//
//                                System.out.println("CAN’T START THE SIMULATION! Please add the fuzzy sets and rules first.");
//                            }
//                            break ;
//                    }
//                }
//
//            }
//
//
//        }
        // take input from file and compute fuzzy logic
//        File file = new File("input.txt");
//        Scanner scan = new Scanner(file);
//        String statement = "" ;
//        String name = "" ;
//        String description = "" ;
//        int choice = 0 ;
//        boolean flag_var = false ;
//        boolean flag_set = false ;
//        boolean flag_rule = false ;
//        Fuzzy_toolbox toolbox = null ;
//        while (scan.hasNextLine())
//        {
//            statement = scan.nextLine() ;
//            switch (statement) {
//                case "1":
//                    name = scan.nextLine();
//                    description = scan.nextLine();
//                    toolbox = new Fuzzy_toolbox(name, description);
//                    break;
//                case "2":
//                    flag_var = true;
//                    while (true) {
//                        statement = scan.nextLine();
//                        if (statement.equals("x"))
//                            break;
//                        toolbox.add_variable(statement);
//                    }
//                    break;
//                case "3":
//                    if (flag_var) {
//                        flag_set = true;
//                        name = scan.nextLine();
//                        while (true) {
//                            statement = scan.nextLine();
//                            if (statement.equals("x"))
//                                break;
//                            assert toolbox != null;
//                            toolbox.get_variable(name).add_set(statement);
////                                    for (int i = 0; i < toolbox.variables.size(); i++)
////                                    {
////                                        if (toolbox.variables.get(i).name.equals(name))
////                                            toolbox.variables.get(i).add_set(statement);
////                                    }
//                        }
//                    } else {
//                        System.out.println("please enter variable first !! ");
//                    }
//                    break;
//                case "4":
//                    if (flag_set && flag_var) {
//                        flag_rule = true;
//                        while (true) {
//                            statement = scan.nextLine();
//                            if (statement.equals("x"))
//                                break;
//                            toolbox.add_Rule(statement);
//                        }
//                    } else {
//                        System.out.println("Please add first variable with it's fuzzy sets !!");
//                    }
//                    break;
//                case "5":
//                    if (flag_rule && flag_set && flag_var) {
////                        System.out.println("Enter the crisp values:");
////                        System.out.println("-----------------------");
////                        for (int i = 0; i < toolbox.variables.size(); i++) {
////                            if (toolbox.variables.get(i).mode.equals("IN")) {
////                                System.out.print(toolbox.variables.get(i).name + " :  ");
////                                double crisp_value = scan.nextDouble();
////                                //toolbox.variables.get(i).crisp_value = crisp_value;
////                                for (int j = 0; j < toolbox.variables.get(i).sets.size(); j++) {
////                                    toolbox.variables.get(i).sets.get(j).calc_degree_of_membership(crisp_value);
////                                }
////                            }
////                        }
//                        while (true) {
//
//                            String var_name = scan.nextLine();
//                            if (var_name.equals("x"))
//                                break;
//                            double crisp_value = scan.nextDouble();
//                            String temp = scan.nextLine();
//                            for (int i = 0; i < Objects.requireNonNull(toolbox).variables.size(); i++) {
//                                if (toolbox.variables.get(i).name.equals(var_name)) {
//                                    for (int j = 0; j < toolbox.variables.get(i).sets.size(); j++) {
//                                        toolbox.variables.get(i).sets.get(j).calc_degree_of_membership(crisp_value);
//                                    }
//                                }
//
//                            }
//                        }
//
//                        for (Rules r : toolbox.rules) {
//                            r.setzFuzzy();
//                        }
//
//                        double result = toolbox.result();
//                        System.out.println("Running the simulation…");
//                        System.out.println("Fuzzification => done");
//                        System.out.println("Inference => done");
//                        System.out.println("Defuzzification => done");
//
//                        System.out.println("the result is : " + result);
//
//                    } else {
//
//                        System.out.println("CAN’T START THE SIMULATION! Please add the fuzzy sets and rules first.");
//                    }
//                    break;
//            }
//        }
//
        GUI frame = new GUI();
//

    }
}
// the input.txt should look like this
//1
//Fuzzy toolbox
//Fuzzy toolbox
//2
//IN
//OUT
//x
//3
//IN
//Enter the fuzzy set name, type (TRI/TRAP) and values: (Press x to finish)
//------------------------------------------------------------
//low TRI 0 0 50
//medium TRI 25 50 75
//high TRI 50 100 100
//x
//OUT






/*
 run test :
/////// Variables //////////
proj_funding IN [0,100]
exp_level IN [0,60]
risk OUT [0,100]
sets :
exp_level
beginner TRI 0 15 30
intermediate TRI 15 30 45
expert TRI 30 60 60


proj_funding
very_low TRAP 0 0 10 30
low TRAP 10 30 40 60
medium TRAP 40 60 70 90
high TRAP 70 90 100 100
risk
low TRI 0 25 50
normal TRI 25 50 75
high TRI 50 100 100
///////// Rules ///////
proj_funding high or exp_level expert => risk low
proj_funding medium and exp_level intermediate => risk normal
proj_funding medium and exp_level beginner => risk normal
proj_funding low and exp_level beginner => risk high
proj_funding very_low and_not exp_level expert => risk high

 */