package com.bookspot.app.vendor;

public class Container_Class {

    public static class NewBooking{
        private int tkn, sbk;
        private String bTime, sType, name, cno, UID;

        public NewBooking(int tkn, int sbk, String bTime, String sType, String name, String cno, String UID) {
            this.tkn = tkn;
            this.sbk = sbk;
            this.bTime = bTime;
            this.sType = sType;
            this.name = name;
            this.cno = cno;
            this.UID = UID;
        }

        public NewBooking() {
        }

        public String getUID() {
            return UID;
        }

        public void setUID(String UID) {
            this.UID = UID;
        }

        public int getTkn() {
            return tkn;
        }

        public void setTkn(int tkn) {
            this.tkn = tkn;
        }

        public int getSbk() {
            return sbk;
        }

        public void setSbk(int sbk) {
            this.sbk = sbk;
        }

        public String getbTime() {
            return bTime;
        }

        public void setbTime(String bTime) {
            this.bTime = bTime;
        }

        public String getsType() {
            return sType;
        }

        public void setsType(String sType) {
            this.sType = sType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCno() {
            return cno;
        }

        public void setCno(String cno) {
            this.cno = cno;
        }
    }

    public static class Vendor{
        private String cno, fname, oname, add, email, website, addno, cat, services, image, total_tokens, sdate, stime, UID, rat, ltiming;
        private double lat, lng;

        public Vendor(){}

        public Vendor(String cno, String fname, String oname, String add, String email, String website, String cat,
                      String services, String image, String total_tokens, String sdate, String stime, String addno , String UID , double lat, double lng) {
            this.cno = cno;
            this.fname = fname;
            this.oname = oname;
            this.add = add;
            this.email = email;
            this.website = website;
            this.addno = addno;
            this.cat = cat;
            this.services = services;
            this.image = image;
            this.total_tokens = total_tokens;
            this.sdate = sdate;
            this.stime = stime;
            this.UID = UID;
            this.lat = lat;
            this.lng = lng;
        }

        public Vendor(String cno, String oname, String UID) {
            this.cno = cno;
            this.oname = oname;
            this.UID = UID;
        }

        public String getLtiming() {
            return ltiming;
        }

        public void setLtiming(String ltiming) {
            this.ltiming = ltiming;
        }

        public String getRat() {
            return rat;
        }

        public void setRat(String rat) {
            this.rat = rat;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public String getUID() {
            return UID;
        }

        public void setUID(String UID) {
            this.UID = UID;
        }

        public String getCno() {
            return cno;
        }

        public void setCno(String cno) {
            this.cno = cno;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getOname() {
            return oname;
        }

        public void setOname(String oname) {
            this.oname = oname;
        }

        public String getAdd() {
            return add;
        }

        public void setAdd(String add) {
            this.add = add;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getAddno() {
            return addno;
        }

        public void setAddno(String addno) {
            this.addno = addno;
        }

        public String getCat() {
            return cat;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }

        public String getServices() {
            return services;
        }

        public void setServices(String services) {
            this.services = services;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getTotal_tokens() {
            return total_tokens;
        }

        public void setTotal_tokens(String total_tokens) {
            this.total_tokens = total_tokens;
        }

        public String getSdate() {
            return sdate;
        }

        public void setSdate(String sdate) {
            this.sdate = sdate;
        }

        public String getStime() {
            return stime;
        }

        public void setStime(String stime) {
            this.stime = stime;
        }
    }
}
