import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    ManyToOne,
    JoinColumn,
    CreateDateColumn,
} from 'typeorm';
import { Vulnerability } from './vulnerability.entity'; // Assuming this entity is defined elsewhere
import { VdoSet } from './vdoset.entity'; // Assuming this entity is defined elsewhere

@Entity({name:'vdocharacteristic'})
export class VdoCharacteristic {
    @PrimaryGeneratedColumn({ name: 'vdo_characteristic_id' })
    id: number;

    @ManyToOne(
        () => Vulnerability,
        (vulnerability) => vulnerability.vdoCharacteristics,
    )
    @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
    vulnerability: Vulnerability;

    @CreateDateColumn()
    createdDate: Date;

    @Column()
    vdoLabel: string;

    @Column()
    vdoNounGroup: string;

    @Column({ type: 'double precision' })
    vdoConfidence: number;

    @Column({ nullable: true })
    userId: number;

    @Column()
    isActive: number;

    @ManyToOne(() => VdoSet, (vdoSet) => vdoSet.vdoCharacteristics)
    @JoinColumn({ name: 'vdo_set_id', referencedColumnName: 'vdoSetId' })
    vdoSet: VdoSet;
}
