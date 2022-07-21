package org.codeforiraq.orphanage.pager;

public class Requirement {
    private String orphanageName, requirementDate, description, email, phone;

    public Requirement(String orphanageName, String requirementDate, String description, String email, String phone) {
        this.orphanageName = orphanageName;
        this.requirementDate = requirementDate;
        this.description = description;
        this.email = email;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrphanageName() {
        return orphanageName;
    }

    public void setOrphanageName(String orphanageName) {
        this.orphanageName = orphanageName;
    }

    public String getRequirementDate() {
        return requirementDate;
    }

    public void setRequirementDate(String requirementDate) {
        this.requirementDate = requirementDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
