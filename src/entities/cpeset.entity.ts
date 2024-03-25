import { Entity, Column, PrimaryGeneratedColumn, OneToMany, OneToOne } from 'typeorm';
import { AffectedProduct } from './affectedproduct.entity'; // Assuming AffectedProduct entity is defined
import { VulnerabilityVersion } from './vulnerabilityversion.entity'; // Assuming you have a VulnerabilityVersion entity defined

@Entity({ name: 'cpeset' })
export class CpeSet {
    @PrimaryGeneratedColumn()
    cpeSetId: number;

    @Column({ type: 'timestamp' })
    createdDate: Date;

    @OneToMany(() => AffectedProduct, affectedProduct => affectedProduct.cpeSet, { cascade: true })
    affectedProducts: AffectedProduct[];

    @OneToOne(() => VulnerabilityVersion, vulnerabilityVersion => vulnerabilityVersion.cpeSet)
    vulnerabilityVersion: VulnerabilityVersion;

    @Column({ nullable: true })
    userId: number;

    @Column()
    cveId: string;

}
