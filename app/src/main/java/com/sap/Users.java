package com.sap;


public class Users {


        private String mob;
        private String dob;

        public Users(){

        }

        public Users( String dob, String mob) {
            this.dob=dob;
            this.mob=mob;
        }




        public String getDob()
        {
            return dob;
    }
    public String getPhn(){return mob;}
}
