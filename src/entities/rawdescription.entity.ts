import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    ManyToOne,
    JoinColumn,
    CreateDateColumn,
    ManyToMany,
    JoinTable,
} from 'typeorm';
import { Vulnerability } from './vulnerability.entity'; 
import { Description } from './description.entity';

@Entity({name:'rawdescription'})
export class RawDescription {
    @PrimaryGeneratedColumn({ name: 'raw_description_id' })
    id: number;

    @Column({ type: 'text' })
    rawDescription: string;

    @ManyToOne(
        () => Vulnerability,
        (vulnerability) => vulnerability.rawDescriptions,
    )
    @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
    vulnerability: Vulnerability;

    @CreateDateColumn()
    createdDate: Date;

    @CreateDateColumn({ name: 'published_date' })
    publishedDate: Date;

    @CreateDateColumn({ name: 'last_modified_date' })
    lastModifiedDate: Date;

    @Column()
    sourceUrl: string;

    @Column({ name: 'is_garbage' })
    isGarbage: number;

    @Column()
    sourceType: string;

    @Column()
    parserType: string;

    @ManyToMany(() => Description, (description) => description.rawDescriptions)
    @JoinTable({
        name: 'rawdescriptionjt',
        joinColumns: [{ name: 'raw_description_id' }],
        inverseJoinColumns: [{ name: 'description_id' }],
    })
    descriptions: Description[];
}
