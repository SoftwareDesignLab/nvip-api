import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    ManyToOne,
    JoinColumn,
    OneToOne,
    CreateDateColumn,
} from 'typeorm';
import { Vulnerability } from './vulnerability.entity'; // Import the Vulnerability entity
import { PatchSourceUrl } from './patchsourceurl.entity'; // Assume this entity is defined elsewhere

@Entity({ name: 'patchcommit' })
export class PatchCommit {
    @PrimaryGeneratedColumn()
    commitId: number;

    @OneToOne(
        () => PatchSourceUrl,
        (patchSourceUrl) => patchSourceUrl.patchCommit,
        { nullable: false },
    )
    @JoinColumn({ name: 'source_url_id', referencedColumnName: 'sourceUrlId' })
    sourceUrl: PatchSourceUrl;

    @ManyToOne(
        () => Vulnerability,
        (vulnerability) => vulnerability.patchCommits,
    )
    @JoinColumn({ name: 'cve_id', referencedColumnName: 'cveId' })
    vulnerability: Vulnerability;

    @Column()
    commitSha: string;

    @Column()
    commitMessage: string;

    @Column({ type: 'text' })
    uniDiff: string;

    @Column()
    timeline: string;

    @Column()
    timeToPatch: string;

    @Column()
    linesChanged: number;

    @CreateDateColumn()
    commitDate: Date;
}
