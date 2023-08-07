/**
 * Copyright 2023 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RSAT19CB0000020 awarded by the United
 * States Department of Homeland Security.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.nvip.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table (name="rawdescription")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RawDescription {
    @Id @Column(name="raw_description_id") private int id;

    private String rawDescription;

    @ManyToOne @JoinColumn(name="cve_id", referencedColumnName = "cveId")
    private Vulnerability vulnerability;

    @Basic private LocalDateTime createdDate;
    @Basic private LocalDateTime publishedDate;
    @Basic private LocalDateTime lastModifiedDate;
    private String sourceUrl;
    @Column(name = "is_garbage", columnDefinition = "int")
    private int isGarbage;
    private String sourceType;
    private String parserType;

    public RawDescription(String rawDescription, Vulnerability vuln, LocalDateTime createdDate, LocalDateTime publishedDate, LocalDateTime lastModifiedDate,
                String sourceUrl, int isGarbage, String sourceType, String parserType) {
        this.rawDescription = rawDescription;
        this.vulnerability = vuln;
        this.createdDate = createdDate;
        this.publishedDate = publishedDate;
        this.lastModifiedDate = lastModifiedDate;
        this.sourceUrl = sourceUrl;
        this.isGarbage = isGarbage;
        this.sourceType = sourceType;
        this.parserType = parserType;
    }
}