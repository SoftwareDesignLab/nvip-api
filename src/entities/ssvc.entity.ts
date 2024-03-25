import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    OneToOne,
    JoinColumn,
} from 'typeorm';
import { Vulnerability } from './vulnerability.entity';

@Entity({ name: 'ssvc' })
export class SSVC {
    @PrimaryGeneratedColumn()
    id: number;

    @OneToOne(() => Vulnerability, (vulnerability) => vulnerability.ssvc)
    @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
    vulnerability: Vulnerability;

    @Column({ type: 'tinyint' })
    automatable: boolean;

    @Column()
    exploitStatus: string;

    @Column({ type: 'tinyint', name: 'technical_impact' })
    technicalImpact: boolean;
}
