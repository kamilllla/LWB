package com.example.lwb.Models;

public class Guid {
    public Guid() {
    }

    private String name;
        private double mark;
        private String genre;
        private int color;
        private String image;

        public Guid(String name,String image) { //double mark, String genre,int image) {
            this.name = name;
//            this.mark = mark;
//            this.genre = genre;
            this.image=image;
//            if (mark>6.9){
//                color=R.color.greenLight;
//            }
//            else {
//                if(mark<=6.9 && mark>=5.0){
//                    color = R.color.pinkLight;
//                }
//                else color = R.color.pink;
//            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getMark() {
            return mark;
        }

        public void setMark(double mark) {
            this.mark = mark;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }



        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

}
