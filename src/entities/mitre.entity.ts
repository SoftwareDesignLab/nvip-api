import {
    Entity,
    Column,
    PrimaryColumn,
    OneToOne,
    JoinColumn,
} from 'typeorm';
import { Vulnerability } from './vulnerability.entity';

@Entity({ name: 'mitredata' })
export class MitreData {
    @PrimaryColumn()
    cveId: string;

    @OneToOne(() => Vulnerability, (vulnerability) => vulnerability.mitreData)
    @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
    vulnerability: Vulnerability;

    @Column()
    status: string;

    @Column({type:'datetime'})
    lastModified: Date;
}
