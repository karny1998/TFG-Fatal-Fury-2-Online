package lib.objects;

import lib.Enums.Movement;
import lib.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Russian roulette.
 */
public class russian_roulette {
    /**
     * The Basic.
     */
// Si es una ruleta simple o no, siendo compleja cuando está compuesta por otras ruletas rusas
    private boolean basic = true;
    /**
     * The Basic selections.
     */
// Selecciones simples
    private List<Pair<Double, Movement>> basicSelections = new ArrayList<>();
    /**
     * The Complex selection.
     */
// Selecciones complejas (otras ruletas)
    private List<Pair<Double, russian_roulette>> complexSelection = new ArrayList<>();
    /**
     * The Basic selections original.
     */
// Selecciones simples originales
    private List<Pair<Double, Movement>> basicSelectionsOriginal = new ArrayList<>();
    /**
     * The Complex selection original.
     */
// Selecciones complejas (otras ruletas) originales
    private List<Pair<Double, russian_roulette>> complexSelectionOriginal = new ArrayList<>();
    /**
     * The Total.
     */
// Total de probabilidad en base al que normalizar
    private double total = 0.0;
    /**
     * The Category.
     */
// Categoría de la ruleta, siendo global, una ruleta normal que agrupa todo tipo de movimientos
    private Movement category = Movement.GLOBAL;

    /**
     * Instantiates a new Russian roulette.
     */
// Contructor por defecto
    public russian_roulette(){}

    /**
     * Instantiates a new Russian roulette.
     *
     * @param b the b
     */
// Contructor que se le indica si es compleja o no
    public russian_roulette(boolean b){
        basic = b;
    }

    /**
     * Spin roulette movement.
     *
     * @return the movement
     */
// Devuelve un movimiento resultado de "girar" la ruleta
    public Movement spinRoulette(){
        double r = Math.random();
        double base = 0.0;
        // Si es simple obtiene el resultado directamente en base al número aleatorio y las probabilidades
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
        // Si es compleja obtiene el resultado en base al spinRoulette sacada del número aleatorio y las probabilidades
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

    /**
     * Add component.
     *
     * @param p the p
     * @param m the m
     */
// Se añade una componente a la ruleta básica
    public void addComponent(double p, Movement m){
        if(basic) {
            basicSelectionsOriginal.add(new Pair<>(p, m));
            basicSelections.add(new Pair<>(p, m));
            total += p;
        }
    }

    /**
     * Add component.
     *
     * @param p the p
     * @param r the r
     */
// Se añade una componente a la ruleta compleja
    public void addComponent(double p, russian_roulette r){
        if(!basic){
            complexSelectionOriginal.add(new Pair<>(p, r));
            complexSelection.add(new Pair<>(p, r));
            total += p;
        }
    }

    /**
     * Fill roulette.
     */
// Normaliza las probabilidades
    public void fillRoulette(){
        updateTotal();
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

    /**
     * Clone russian roulette.
     *
     * @return the russian roulette
     */
// Devuelve una ruleta rusa idéntica a sí misma
    public  russian_roulette clone(){
        russian_roulette r = new russian_roulette();
        if(basic) {
            for (int i = 0; i < basicSelectionsOriginal.size(); ++i) {
                r.addComponent(basicSelectionsOriginal.get(i).getKey(),basicSelectionsOriginal.get(i).getValue());
            }
        }
        else{
            for (int i = 0; i < complexSelectionOriginal.size(); ++i) {
                r.addComponent(complexSelectionOriginal.get(i).getKey(),complexSelectionOriginal.get(i).getValue().clone());
                r.setCategory(category);
            }
        }
        return r;
    }

    /**
     * Is basic boolean.
     *
     * @return the boolean
     */
// Getters y setters
    public boolean isBasic() {
        return basic;
    }

    /**
     * Sets basic.
     *
     * @param basic the basic
     */
    public void setBasic(boolean basic) {
        this.basic = basic;
    }

    /**
     * Gets basic selections.
     *
     * @return the basic selections
     */
    public List<Pair<Double, Movement>> getBasicSelections() {
        return basicSelections;
    }

    /**
     * Sets basic selections.
     *
     * @param basicSelections the basic selections
     */
    public void setBasicSelections(List<Pair<Double, Movement>> basicSelections) {
        this.basicSelections = basicSelections;
    }

    /**
     * Gets complex selection.
     *
     * @return the complex selection
     */
    public List<Pair<Double, russian_roulette>> getComplexSelection() {
        return complexSelection;
    }

    /**
     * Sets complex selection.
     *
     * @param complexSelection the complex selection
     */
    public void setComplexSelection(List<Pair<Double, russian_roulette>> complexSelection) {
        this.complexSelection = complexSelection;
    }

    /**
     * Gets total.
     *
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * Sets total.
     *
     * @param total the total
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Update total.
     */
    public void updateTotal(){
        total = 0.0;
        if(basic){
            for(int i = 0; i < basicSelections.size(); ++i){
                total += basicSelections.get(i).getKey();
            }
        }
        else{
            for(int i = 0; i < complexSelection.size(); ++i){
                total += complexSelection.get(i).getKey();
                complexSelection.get(i).getValue().updateTotal();
            }
        }
    }

    /**
     * Gets basic selections original.
     *
     * @return the basic selections original
     */
    public List<Pair<Double, Movement>> getBasicSelectionsOriginal() {
        return basicSelectionsOriginal;
    }

    /**
     * Sets basic selections original.
     *
     * @param basicSelectionsOriginal the basic selections original
     */
    public void setBasicSelectionsOriginal(List<Pair<Double, Movement>> basicSelectionsOriginal) {
        this.basicSelectionsOriginal = basicSelectionsOriginal;
    }

    /**
     * Gets complex selection original.
     *
     * @return the complex selection original
     */
    public List<Pair<Double, russian_roulette>> getComplexSelectionOriginal() {
        return complexSelectionOriginal;
    }

    /**
     * Sets complex selection original.
     *
     * @param complexSelectionOriginal the complex selection original
     */
    public void setComplexSelectionOriginal(List<Pair<Double, russian_roulette>> complexSelectionOriginal) {
        this.complexSelectionOriginal = complexSelectionOriginal;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public Movement getCategory() {
        return category;
    }

    /**
     * Sets category.
     *
     * @param category the category
     */
    public void setCategory(Movement category) {
        this.category = category;
    }
}
