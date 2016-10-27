package khaja.reopti1;

public class Recharge implements Comparable<Recharge> {
    static final Double LOCAL_TARIFF = 2.0;
    static final Double STD_TARIFF = 2.0;
    Double rechargeCost;
    Double validity;
    String location;
    String pulseType;
    Double pulseBundle;
    Double costPerPulse;
    Double firstXPulses;
    Double costPerFirstXPulses;
    Double monthlyCost;

    String description;

    public String getDescription() {
        return description;
    }

    public Double getMonthlyCost() {
        return monthlyCost;
    }

    public Double getRechargeCost() {
        return rechargeCost;
    }

    public Double getValidity() {
        return validity;
    }

    public Recharge(Double rechargeCost, Double validity, String location, String pulseType, Double pulseBundle, String description){
        this.rechargeCost = rechargeCost;
        this.validity = validity;
        this.location = location;
        this.pulseType = pulseType;
        this.pulseBundle = pulseBundle;
        this.description = description;
        this.costPerPulse = this.firstXPulses = this.costPerFirstXPulses = 0.0;
    }

    public Recharge(Double rechargeCost, Double validity, String location, String pulseType,
                    Double costPerPulse, Double firstXPulses, Double costPerFirstXPulses, String description){
        this.rechargeCost = rechargeCost;
        this.validity = validity;
        this.location = location;
        this.pulseType = pulseType;
        this.costPerPulse = costPerPulse;
        this.firstXPulses = firstXPulses;
        this.costPerFirstXPulses = costPerFirstXPulses;
        this.description = description;
        this.pulseBundle = 0.0;
    }

    public Double calculateMonthlyCost(Stats stats){
        Double monthlyCost = 0.0;
        int userPulses = 0;
        int seconds = 0;


        switch (this.location) {
            case "LA":
                monthlyCost += LOCAL_TARIFF * stats.getLocalOtherSeconds() / (double)stats.getDays() * 30.0;
                monthlyCost += STD_TARIFF * stats.getStdSeconds() / (double)stats.getDays() * 30.0;;
                seconds = stats.getLocalSameSeconds();
                if (pulseType.equals("m")) userPulses = stats.getLocalSameMinutes();
                else userPulses = stats.getLocalSameSeconds();
                break;
            case "L":
                monthlyCost += STD_TARIFF * stats.getStdSeconds() / (double)stats.getDays() * 30.0;;
                seconds = stats.getLocalTotalSeconds();
                if (pulseType.equals("m")) userPulses = stats.getLocalTotalMinutes();
                else userPulses = stats.getLocalTotalSeconds();
                break;
            case "S":
                monthlyCost += LOCAL_TARIFF * stats.getLocalTotalSeconds() / (double)stats.getDays() * 30.0;
                seconds = stats.getStdSeconds();
                if (pulseType.equals("m")) userPulses = stats.getStdMinutes();
                else userPulses = stats.getStdSeconds();
                break;
            case "ALL":
                seconds = stats.getStdSeconds() + stats.getLocalTotalSeconds();
                if (pulseType.equals("m")) userPulses = stats.getStdMinutes() + stats.getLocalTotalMinutes();
                else userPulses = stats.getStdSeconds() + stats.getLocalTotalSeconds();
                break;
        }


        if (!this.pulseBundle.equals(0.0)){
            Double rateOfPulseUse = (double)userPulses/(double)stats.getDays();
            Double actualValidity = Math.min(validity,this.pulseBundle/rateOfPulseUse);
            monthlyCost += (this.rechargeCost/actualValidity)*30.0*100.0;
            this.monthlyCost = monthlyCost;
            return monthlyCost;
        }

        monthlyCost += (this.rechargeCost/this.validity)*30.0*100.0;

        if (this.firstXPulses.equals(0.0)){
            Double rateOfPulseUse = (double)userPulses/(double)stats.getDays();
            monthlyCost += this.costPerPulse * rateOfPulseUse * 30.0;
            this.monthlyCost = monthlyCost;
            return monthlyCost;
        }

        if (this.pulseType.equals("m"))this.firstXPulses /= 60.0;
        Double rateOfPulseUse = (double)userPulses/(double)stats.getDays();

        if (rateOfPulseUse.compareTo(firstXPulses) < 0){
            monthlyCost += this.costPerFirstXPulses * (double)seconds / (double)stats.getDays() * 30.0;
            this.monthlyCost = monthlyCost;
            return monthlyCost;
        }else {
            if (this.pulseType.equals("m"))this.firstXPulses *= 60.0;
            monthlyCost += this.firstXPulses * this.costPerFirstXPulses *30.0;
            if (this.pulseType.equals("m"))this.firstXPulses /= 60.0;
            userPulses -= stats.getDays() * (int)(double)this.firstXPulses;
            monthlyCost += (double)userPulses / (double)stats.getDays() * this.costPerPulse * 30.0;
            this.monthlyCost = monthlyCost;
            return monthlyCost;
        }

    }

    @Override
    public int compareTo(Recharge recharge) {
        return this.monthlyCost.compareTo(recharge.getMonthlyCost());
    }
}