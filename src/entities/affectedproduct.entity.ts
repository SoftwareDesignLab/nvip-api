import {
    Entity,
    Column,
    PrimaryGeneratedColumn,
    ManyToOne,
    JoinColumn,
} from 'typeorm';
import { Exclude } from 'class-transformer';
import { Vulnerability } from './vulnerability.entity'; // Ensure to adjust the import path
import { CpeSet } from './cpeset.entity'; // Ensure to adjust the import path

@Entity('affectedproduct')
export class AffectedProduct {
    @PrimaryGeneratedColumn()
    affectedProductId: number;

    @ManyToOne(() => Vulnerability, (vulnerability) => vulnerability.affectedProducts)
    @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
    vulnerability: Vulnerability;

    @Exclude()
    @ManyToOne(() => CpeSet, (cpeSet) => cpeSet.affectedProducts)
    @JoinColumn({ name: 'cpe_set_id' })
    cpeSet: CpeSet;

    @Column()
    cpe: string;

    @Column()
    productName: string;

    @Column()
    version: string;

    @Column()
    vendor: string;

    @Column()
    purl: string;

    @Column({ nullable: true })
    swidTag: string;
}
