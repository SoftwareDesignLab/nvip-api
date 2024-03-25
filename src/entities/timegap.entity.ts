import { Entity, PrimaryGeneratedColumn, Column, ManyToOne, JoinColumn, CreateDateColumn } from 'typeorm';
import { Vulnerability } from './vulnerability.entity'; 

@Entity({ name: 'timegap' })
export class Timegap {
  @PrimaryGeneratedColumn()
  timegapId: number;

  @ManyToOne(() => Vulnerability, (vulnerability) => vulnerability.timegaps)
  @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
  vulnerability: Vulnerability;

  @Column()
  location: string; // "nvd" or "mitre"

  @Column({ type: 'double precision' })
  timegap: number; // Measured in hours

  @CreateDateColumn()
  createdDate: Date;

}
