import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    CreateDateColumn,
    OneToOne,
    JoinColumn,
    ManyToMany,
    JoinTable,
} from 'typeorm';
import { VulnerabilityVersion } from './vulnerabilityversion.entity'; 
import { RawDescription } from './rawdescription.entity'; 

@Entity()
export class Description {
    @PrimaryGeneratedColumn({name:"description_id"})
    descriptionId: number;

    @Column({ type: 'text' })
    description: string;

    @CreateDateColumn()
    createdDate: Date;

    @Column()
    gptFunc: string;

    @Column()
    cveId: string;

    @Column()
    isUserGenerated: number;

    @OneToOne(() => VulnerabilityVersion, (vulnerabilityVersion) => vulnerabilityVersion.description)
    vulnerabilityVersion: VulnerabilityVersion;

    @ManyToMany(
        () => RawDescription,
        (rawDescription) => rawDescription.descriptions,
    )
    @JoinTable({
        name: 'rawdescriptionjt',
        joinColumns: [{ name: 'description_id' }],
        inverseJoinColumns: [{ name: 'raw_description_id' }],
    })
    rawDescriptions: RawDescription[];
}
