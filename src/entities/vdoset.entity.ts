import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    OneToMany,
    OneToOne,
    JoinColumn,
    CreateDateColumn,
} from 'typeorm';
import { VdoCharacteristic } from './vdocharacteristic.entity'; // Assuming this entity is defined elsewhere
import { VulnerabilityVersion } from './vulnerabilityversion.entity'; // Assuming this entity is defined elsewhere

@Entity({ name: 'vdoset' })
export class VdoSet {
    @PrimaryGeneratedColumn()
    vdoSetId: number;

    @CreateDateColumn()
    createdDate: Date;

    @OneToMany(
        () => VdoCharacteristic,
        (vdoCharacteristic) => vdoCharacteristic.vdoSet,
        { cascade: true },
    )
    @JoinColumn({ name: 'vdo_set_id' }) // This column is in the VdoCharacteristic entity pointing back to VdoSet
    vdoCharacteristics: VdoCharacteristic[];

    @OneToOne(
        () => VulnerabilityVersion,
        (vulnerabilityVersion) => vulnerabilityVersion.vdoSet,
    ) // Assuming the VulnerabilityVersion entity correctly points back to VdoSet
    vulnerabilityVersion: VulnerabilityVersion;

    @Column({ type: 'float' })
    cvssBaseScore: number;

    @Column({ nullable: true })
    userId: number;

    @Column()
    cveId: string;

}
