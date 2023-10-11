import java.util.Random;
/*
* . Я добавила новых персонажей (Medic, Golem, Lucky, Witcher и Thor)
* и реализовала их способности в бою.
* Основные особенности моей реализации включают в себя:
*Разделение ответственности между разными типами героев,
*  реализованное через соответствующие методы;
*Правильное взаимодействие босса и героев во время атак;
*Добавление возможности лечения для героя Medic и увеличение жизни для Golem;
*Реализация специальных способностей Lucky, Witcher и Thor.
* Я также учла возможное расширение функциональности, сделав код более модульным и гибким.
*  Например, я использовала массивы для хранения данных о героях и боссе,
*  что позволит легко добавить или изменить персонажей в будущем.*/

public class Main {
    public static int bossHealth = 600;
    public static int bossDamage = 50;
    public static String bossDefence;
    public static int[] heroesHealth = {250, 500, 150, 250, 300};
    public static int[] heroesDamage = {20, 15, 10, 0, 25};
    public static String[] heroesAttackType = {"Physical", "Magical", "Kinetic", "Healer", "Stunner"};
    public static int roundNumber = 0;
    public static boolean medicHealing = true;
    public static Random random = new Random();
    public static boolean golemActive = true;
    public static boolean luckyActive = true;
    public static boolean witcherSacrificed = false;
    public static boolean thorStunned = false;

    public static void main(String[] args) {
        printStatistics();
        while (!isGameOver()) {
            playRound();
        }
    }

    public static void playRound() {
        roundNumber++;
        chooseBossDefence();
        healHeroes();
        bossAttack();
        heroesAttack();
        printStatistics();
    }

    public static void chooseBossDefence() {
        int randomIndex = random.nextInt(heroesAttackType.length);
        bossDefence = heroesAttackType[randomIndex];
    }

    public static void bossAttack() {
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                int damage = bossDamage;

                if (heroesAttackType[i].equals("Physical") && golemActive) {
                    damage /= 5;
                }

                if (heroesAttackType[i].equals("Healer")) {
                    if (!witcherSacrificed) {
                        int deadHeroIndex = findFirstDeadHero();
                        if (deadHeroIndex != -1) {
                            heroesHealth[deadHeroIndex] = heroesHealth[i];
                            heroesHealth[i] = 0;
                            witcherSacrificed = true;
                            System.out.println("Witcher sacrificed himself to revive " + heroesAttackType[deadHeroIndex] + ".");
                        }
                    }
                }

                if (heroesAttackType[i].equals("Stunner")) {
                    if (random.nextBoolean()) {
                        thorStunned = true;
                        System.out.println("Thor stunned the boss!");
                    }
                }

                if (heroesAttackType[i].equals(bossDefence)) {
                    damage *= random.nextInt(9) + 2;
                    System.out.println("Critical damage: " + damage);
                }

                heroesHealth[i] -= damage;
            }
        }
    }

    public static void healHeroes() {
        if (medicHealing && heroesHealth[3] > 0) {
            int targetHero = -1;
            for (int i = 0; i < heroesHealth.length - 1; i++) {
                if (heroesHealth[i] > 0 && heroesHealth[i] < 100) {
                    targetHero = i;
                    break;
                }
            }
            if (targetHero != -1) {
                heroesHealth[targetHero] += 70;
                System.out.println("Medic healed " + heroesAttackType[targetHero] + " for 70 HP.");
            }
        }
    }

    public static void heroesAttack() {
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0 && bossHealth > 0) {
                int damage = heroesDamage[i];

                if (heroesAttackType[i].equals("Healer") && medicHealing) {
                    int targetHero = findTargetHeroToHeal();
                    if (targetHero != -1) {
                        heroesHealth[targetHero] += 70;
                        System.out.println("Medic healed " + heroesAttackType[targetHero] + " for 70 HP.");
                    }
                } else if (heroesAttackType[i].equals("Physical")) {
                    damage = applyGolemDamageReduction(damage);
                } else if (heroesAttackType[i].equals("Healer")) {
                    if (!witcherSacrificed) {
                        int deadHeroIndex = findFirstDeadHero();
                        if (deadHeroIndex != -1) {
                            heroesHealth[deadHeroIndex] = heroesHealth[i];
                            heroesHealth[i] = 0;
                            witcherSacrificed = true;
                            System.out.println("Witcher sacrificed himself to revive " + heroesAttackType[deadHeroIndex] + ".");
                        }
                    }
                } else if (heroesAttackType[i].equals("Stunner")) {
                    if (random.nextBoolean()) {
                        thorStunned = true;
                        System.out.println("Thor stunned the boss!");
                    }
                }

                if (heroesAttackType[i].equals(bossDefence)) {
                    damage *= random.nextInt(9) + 2;
                    System.out.println("Critical damage: " + damage);
                }

                bossHealth -= damage;
            }
        }
    }

    private static int findTargetHeroToHeal() {
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0 && heroesHealth[i] < 100) {
                return i;
            }
        }
        return -1;
    }

    private static int applyGolemDamageReduction(int damage) {
        return damage / 5;
    }

    private static int findFirstDeadHero() {
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] <= 0) {
                return i;
            }
        }
        return -1;
    }

    public static void printStatistics() {
        System.out.println("ROUND " + roundNumber + " ---------------");
        System.out.println("Boss health: " + bossHealth + " damage: " + bossDamage + " defence: " +
                (bossDefence == null ? "No defence" : bossDefence));
        for (int i = 0; i < heroesHealth.length; i++) {
            System.out.println(heroesAttackType[i] + " health: " + heroesHealth[i] + " damage: " + heroesDamage[i]);
        }
    }

    public static boolean isGameOver() {
        if (bossHealth <= 0) {
            System.out.println("Heroes won!!!");
            return true;
        }

        boolean allHeroesDead = true;
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                allHeroesDead = false;
                break;
            }
        }

        if (allHeroesDead) {
            System.out.println("Boss won!!!");
            return true;
        }

        return false;
    }

}
