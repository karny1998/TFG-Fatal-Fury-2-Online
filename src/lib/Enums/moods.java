package lib.Enums;

/**
 * The type Moods.
 */
public class moods {
    /**
     * The Desperate agressive.
     */
// La suma de los valores absolutos debe ser máximo 1
    // Los signos negativos únicamente implican si se desea usar la variable normal
    // o la complementaria, p.e. + -> tiempo/90, - -> 1 - tiempo/90
    // Orden: tiempo, vida jugador, vida ia, número de ronda, victorias jugador
    public double DESPERATE_AGRESSIVE[] = {-0.25, -0.35, 0.0, 0.20, 0.20};
    /**
     * The Desperate defensive.
     */
    public double DESPERATE_DEFENSIVE[] = {-0.25, 0.0, -0.35, 0.20, 0.20};
    /**
     * The Patient agressive.
     */
    public double PATIENT_AGRESSIVE[] = {-0.40, -0.40, 0.0, 0.20, 0.0};
    /**
     * The Patient defensive.
     */
    public double PATIENT_DEFENSIVE[] = {-0.40, 0.0, -0.40, 0.20, 0.0};
    /**
     * The Impatient agressive.
     */
    public double IMPATIENT_AGRESSIVE[] = {0.40, 30.0, 30, 0.0, 0.0};
}
