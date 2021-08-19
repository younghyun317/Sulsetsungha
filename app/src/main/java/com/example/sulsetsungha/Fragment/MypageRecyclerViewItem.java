package com.example.sulsetsungha.Fragment;

class MypageRecyclerViewItem {
    private String id;
    private String company;
    private String title;
    private String context;
    private String dday;
    private String donation;

    private int amount;

    public String getId() { return id; }

    public void setId (String id) { this.id = id; }

    public String getCompany() {
        return company;
    }

    public void setCompany (String company) { this.company = company; }

    public String getTitle() {
        return title;
    }

    public void setTitle (String title) { this.title = title; }

    public String getContext() { return context; }

    public void setContext (String context) { this.context = context; }

    public String getDday() {
        return dday;
    }

    public void setDday (String dday) { this.dday = dday; }

    public String getDonation() {
        return donation;
    }

    public void setDonation (String donation) { this.donation = donation; }

    public int getAmount() { return amount; }

    public void setAmount(int amount) { this.amount = amount; }
}
