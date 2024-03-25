import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    CreateDateColumn,
} from 'typeorm';

@Entity({ name: 'runhistory' })
export class RunHistory {
    @PrimaryGeneratedColumn({ name: 'runhistory_id' })
    runhistoryId: number;

    @CreateDateColumn({ name: 'runDateTime' })
    runDateTime: Date;

    @Column()
    totalCveCount: number;

    @Column()
    newCveCount: number;

    @Column()
    updatedCveCount: number;

    @Column()
    notInNvdCount: number;

    @Column()
    notInMitreCount: number;

    @Column()
    notInBothCount: number;

    @Column({ type: 'double precision' })
    avgTimeGapNvd: number;

    @Column({ type: 'double precision' })
    avgTimeGapMitre: number;
}
