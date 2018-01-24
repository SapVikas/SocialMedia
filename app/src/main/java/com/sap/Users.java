package com.sap;


public class Users {


        private String mob,name;
        private String dob;

        public Users(){

        }

        public Users( String name,String dob, String mob) {
            this.dob=dob;
            this.name=name;
            this.mob=mob;
        }



public String getName(){return name;}
        public String getDob()
        {
            return dob;
    }
    public String getPhn(){return mob;}
}
