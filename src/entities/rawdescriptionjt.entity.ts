/*import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    OneToOne,
    JoinColumn,
} from 'typeorm';
import { Description } from './description.entity'; 
import { RawDescription } from './rawdescription.entity'; 

@Entity({ name: 'rawdescriptionjt' })
export class RawDescriptionJT {
    @OneToOne(() => Description)
    @JoinColumn({ name: 'description_id' })
    description: Description;

    @OneToOne(() => RawDescription)
    @JoinColumn({ name: 'raw_description_id' })
    rawDescription: RawDescription; 
}*/
