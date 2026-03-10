package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarTest {

    @Test
    void testCarGettersAndSetters() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("Roadster");
        car.setCarColor("Red");
        car.setCarQuantity(3);

        assertEquals("car-1", car.getCarId());
        assertEquals("Roadster", car.getCarName());
        assertEquals("Red", car.getCarColor());
        assertEquals(3, car.getCarQuantity());
    }
}
