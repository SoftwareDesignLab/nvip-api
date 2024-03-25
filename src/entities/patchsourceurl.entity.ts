import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    OneToOne,
    JoinColumn,
} from 'typeorm';
import { PatchCommit } from './patchcommit.entity'; 

@Entity({ name: 'patchsourceurl' })
export class PatchSourceUrl {
    @PrimaryGeneratedColumn()
    sourceUrlId: number;

    @OneToOne(() => PatchCommit, (patchCommit) => patchCommit.sourceUrl)
    patchCommit: PatchCommit;

    @Column()
    sourceUrl: string;
}
