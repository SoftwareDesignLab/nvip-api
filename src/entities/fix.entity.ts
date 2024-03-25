import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, JoinColumn } from 'typeorm';
import { Vulnerability } from './vulnerability.entity'; // Import the related Vulnerability entity

@Entity({ name: 'fixes' })
export class Fix {
  @PrimaryGeneratedColumn()
  fixId: number;

  @ManyToOne(() => Vulnerability, vulnerability => vulnerability.fixes)
  @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
  vulnerability: Vulnerability;

  @Column()
  fixDescription: string;

  @Column()
  sourceUrl: string;
}
