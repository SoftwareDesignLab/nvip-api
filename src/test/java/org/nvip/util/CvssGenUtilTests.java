package org.nvip.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nvip.entities.VdoCharacteristic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CvssGenUtilTests {

    @Autowired CvssGenUtil cvssGenUtil;

    @Test
    public void testCalculateCvss(){
        List<VdoCharacteristic> characteristics = new ArrayList<>();

        characteristics.add(new VdoCharacteristic(null, "Read", "Logical Impact", 0));
        characteristics.add(new VdoCharacteristic(null, "Resource Removal", "Logical Impact", 0));
        characteristics.add(new VdoCharacteristic(null, "Limited Rmt", "Attack Theater", 0));
        characteristics.add(new VdoCharacteristic(null, "Sandboxed", "Mitigation", 0));
        characteristics.add(new VdoCharacteristic(null, "Physical Security", "Mitigation", 0));
        characteristics.add(new VdoCharacteristic(null, "Application", "Context", 0));
        characteristics.add(new VdoCharacteristic(null, "Trust Failure", "Impact Method", 0));

        Double score = cvssGenUtil.calculateCVSSScore(characteristics);

        assertEquals(7.4, score);
    }
}
