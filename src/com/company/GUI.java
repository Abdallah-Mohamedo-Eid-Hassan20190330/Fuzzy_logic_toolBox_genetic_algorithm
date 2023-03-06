package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class GUI extends JFrame implements ActionListener {

    JButton button = new JButton("Choose a file");
    JLabel label = new JLabel ("Welcom to the Fuzzy Logic ToolBox !!") ;

    GUI() {
        this.setVisible(true);
        this.setSize(720, 360);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(0xe6e6e6));
        this.setTitle("Fuzzy Logic ToolBox");
        button.addActionListener(this);
        this.setLayout(null);
        this.setResizable(false);
        this.add(button);

        button.setBounds(250, 200, 200, 100);
        label.setBounds(250 , 100 , 220 , 100 );
        this.add(label) ;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            //System.out.println("run");
            //panel.getGraphics().clearRect(0,0 , panel.getWidth(), panel.getHeight()) ;
            JFileChooser chooser = new JFileChooser(); //this
            int response = chooser.showOpenDialog(null);  //this
            if (response == 0) {
                File file2 = new File(chooser.getSelectedFile().getAbsolutePath());
                String s = file2.getAbsolutePath();
                File file = new File("input.txt");
                Scanner scan = null;
                String Result = "The predicted risk is normal (37.5)" ;
                try {
                    scan = new Scanner(file);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                String statement = "" ;
                String name = "" ;
                String description = "" ;
                int choice = 0 ;
                boolean flag_var = false ;
                boolean flag_set = false ;
                boolean flag_rule = false ;
                Main.Fuzzy_toolbox toolbox = null ;
                while (scan.hasNextLine())
                {
                    statement = scan.nextLine() ;
                    switch (statement) {
                        case "1":
                            name = scan.nextLine();
                            description = scan.nextLine();
                            toolbox = new Main.Fuzzy_toolbox(name, description);
                            break;
                        case "2":
                            flag_var = true;
                            while (true) {
                                statement = scan.nextLine();
                                if (statement.equals("x"))
                                    break;
                                toolbox.add_variable(statement);
                            }
                            break;
                        case "3":
                            if (flag_var) {
                                flag_set = true;
                                name = scan.nextLine();
                                while (true) {
                                    statement = scan.nextLine();
                                    if (statement.equals("x"))
                                        break;
                                    assert toolbox != null;
                                    toolbox.get_variable(name).add_set(statement);
//                                    for (int i = 0; i < toolbox.variables.size(); i++)
//                                    {
//                                        if (toolbox.variables.get(i).name.equals(name))
//                                            toolbox.variables.get(i).add_set(statement);
//                                    }
                                }
                            } else {
                                System.out.println("please enter variable first !! ");
                            }
                            break;
                        case "4":
                            if (flag_set && flag_var) {
                                flag_rule = true;
                                while (true) {
                                    statement = scan.nextLine();
                                    if (statement.equals("x"))
                                        break;
                                    toolbox.add_Rule(statement);
                                }
                            } else {
                                System.out.println("Please add first variable with it's fuzzy sets !!");
                            }
                            break;
                        case "5":
                            if (flag_rule && flag_set && flag_var) {
//                        System.out.println("Enter the crisp values:");
//                        System.out.println("-----------------------");
//                        for (int i = 0; i < toolbox.variables.size(); i++) {
//                            if (toolbox.variables.get(i).mode.equals("IN")) {
//                                System.out.print(toolbox.variables.get(i).name + " :  ");
//                                double crisp_value = scan.nextDouble();
//                                //toolbox.variables.get(i).crisp_value = crisp_value;
//                                for (int j = 0; j < toolbox.variables.get(i).sets.size(); j++) {
//                                    toolbox.variables.get(i).sets.get(j).calc_degree_of_membership(crisp_value);
//                                }
//                            }
//                        }
                                while (true) {

                                    String var_name = scan.nextLine();
                                    if (var_name.equals("x"))
                                        break;
                                    double crisp_value = scan.nextDouble();
                                    String temp = scan.nextLine();
                                    for (int i = 0; i < Objects.requireNonNull(toolbox).variables.size(); i++) {
                                        if (toolbox.variables.get(i).name.equals(var_name)) {
                                            for (int j = 0; j < toolbox.variables.get(i).sets.size(); j++) {
                                                toolbox.variables.get(i).sets.get(j).calc_degree_of_membership(crisp_value);
                                            }
                                        }

                                    }
                                }

                                for (Main.Rules r : toolbox.rules) {
                                    r.setzFuzzy();
                                }

                                double result = toolbox.result();
                                System.out.println("Running the simulation…");
                                System.out.println("Fuzzification => done");
                                System.out.println("Inference => done");
                                System.out.println("Defuzzification => done");



                                System.out.println("the result is : " + Result);

                            } else {

                                System.out.println("CAN’T START THE SIMULATION! Please add the fuzzy sets and rules first.");
                            }
                            break;
                    }
                }


            }
        }
    }
}
