import { Entity, Column, PrimaryGeneratedColumn, ManyToOne, JoinColumn } from 'typeorm';
import { Vulnerability } from './vulnerability.entity'; // Ensure you have this entity defined

@Entity({ name: 'cvss' })
export class Cvss {
    @PrimaryGeneratedColumn({ name: 'cvss_id' })
    id: number;

    @ManyToOne(() => Vulnerability, vulnerability => vulnerability.cvssScores) // Adjust the second parameter based on the Vulnerability entity's relation field
    @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
    vulnerability: Vulnerability;

    @Column({ type: 'double precision' }) 
    baseScore: number;

    impactScore: number;

    @Column({ type: 'timestamp', nullable: true }) 
    createDate: Date;

    @Column({ nullable: true })
    userId: number;

}
