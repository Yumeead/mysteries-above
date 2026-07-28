package me.vangoo.domain.valueobjects;

/**
 * Збережена морська мітка (Морська Пам'ять, Tyrant). Чистий VO — світ і координати
 * зберігаємо примітивами, конвертацію в {@code Location} робить ефект-шар.
 */
public record Waypoint(String name, String world, double x, double y, double z) {

    public Waypoint withName(String newName) {
        return new Waypoint(newName, world, x, y, z);
    }
}
