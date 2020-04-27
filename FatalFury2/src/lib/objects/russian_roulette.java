package lib.objects;

import javafx.util.Pair;
import lib.Enums.Movement;

import java.util.ArrayList;
import java.util.List;

public class russian_roulette {
    private boolean basic = true;
    private List<Pair<Double, Movement>> basicSelections = new ArrayList<>();
    private List<Pair<Double, russian_roulette>> complexSelection = new ArrayList<>();
    private double total = 0.0;
    private Movement category = Movement.GLOBAL;

    public russian_roulette(){}

    public russian_roulette(boolean b){
        basic = b;
    }

    public Movement spinRoulette(){
        double r = Math.random();
        double base = 0.0;
        if(basic){
            if(basicSelections.size() == 0){
                return Movement.STANDING;
            }
            for(int i = 0; i < basicSelections.size(); ++i){
                double p = basicSelections.get(i).getKey();
                if(r >= base && r <= base+p){
                    return basicSelections.get(i).getValue();
                }
                else{
                    base += p;
                }
            }
        }
        else{
            if(complexSelection.size() == 0){
                return Movement.STANDING;
            }
            for(int i = 0; i < complexSelection.size(); ++i){
                double p = complexSelection.get(i).getKey();
                if(r >= base && r <= base+p){
                    return complexSelection.get(i).getValue().spinRoulette();
                }
                else{
                    base += p;
                }
            }
        }
        return Movement.STANDING;
    }

    public void addComponent(double p, Movement m){
        if(basic) {
            basicSelections.add(new Pair<>(p, m));
            total += p;
        }
    }

    public void addComponent(double p, russian_roulette r){
        if(!basic){
            complexSelection.add(new Pair<>(p, r));
            total += p;
        }
    }

    public void fillRoulette(){
        if(basic){
            for(int i = 0; i < basicSelections.size(); ++i){
                Pair<Double, Movement> aux = basicSelections.get(i);
                basicSelections.remove(i);
                basicSelections.add(i,new Pair<>(aux.getKey()/total,aux.getValue()));
            }
        }
        else{
            for(int i = 0; i < complexSelection.size(); ++i){
                Pair<Double, russian_roulette> aux = complexSelection.get(i);
                complexSelection.remove(i);
                complexSelection.add(i,new Pair<>(aux.getKey()/total,aux.getValue()));
            }
        }
    }

    public boolean isBasic() {
        return basic;
    }

    public void setBasic(boolean basic) {
        this.basic = basic;
    }

    public List<Pair<Double, Movement>> getBasicSelections() {
        return basicSelections;
    }

    public void setBasicSelections(List<Pair<Double, Movement>> basicSelections) {
        this.basicSelections = basicSelections;
    }

    public List<Pair<Double, russian_roulette>> getComplexSelection() {
        return complexSelection;
    }

    public void setComplexSelection(List<Pair<Double, russian_roulette>> complexSelection) {
        this.complexSelection = complexSelection;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
