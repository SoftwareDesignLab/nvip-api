/ **
* Copyright 2021 Rochester Institute of Technology (RIT). Developed with
* government support under contract 70RCSA22C00000008 awarded by the United
* States Department of Homeland Security for Cybersecurity and Infrastructure Security Agency.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the “Software”), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
* /

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
