import {
    Entity,
    Column,
    PrimaryColumn,
    OneToOne,
    JoinColumn,
    PrimaryGeneratedColumn,
    OneToMany,
    ManyToOne,
} from 'typeorm';
import { Vulnerability } from './vulnerability.entity';

@Entity({ name: 'nvddata' })
export class NvdData {
    @PrimaryColumn()
    cveId: string;

    @OneToOne(() => Vulnerability, (vulnerability) => vulnerability.nvdData)
    @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
    vulnerability: Vulnerability;

    @Column()
    status: string;

    @Column({ type: 'datetime' })

    publishedDate: Date;
    @Column({ type: 'datetime' })
    lastModified: Date;

    @OneToMany(() =>NvdSourceUrl, (sourceUrl) => sourceUrl.nvdData)
    sourceUrl:NvdSourceUrl[];
}

@Entity({ name: 'nvdsourceurl' })
export class NvdSourceUrl {
    @PrimaryGeneratedColumn()
    sourceId: number;

    @ManyToOne(() => NvdData, (nvdData) => nvdData.sourceUrl)
    @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
    nvdData: NvdData;

    @Column()
    sourceUrl: string;

  

  


}
